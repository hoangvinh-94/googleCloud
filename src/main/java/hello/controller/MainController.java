package hello.controller;

import hello.storage.StorageFileNotFoundException;
import hello.storage.StorageService;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import hello.model.TinTuc;
import hello.service.TinTucService;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;

@Controller
public class MainController {

	// Upload file to google drive

	/** Application name. */
	private static final String APPLICATION_NAME =
			"Drive API Java Quickstart";

	/** Directory to store user credentials for this application. */
	private static final java.io.File DATA_STORE_DIR = new java.io.File(
			System.getProperty("user.home"), ".credentials/drive-java-quickstart");

	/** Global instance of the {@link FileDataStoreFactory}. */
	private static FileDataStoreFactory DATA_STORE_FACTORY;

	/** Global instance of the JSON factory. */
	private static final JsonFactory JSON_FACTORY =
			JacksonFactory.getDefaultInstance();

	/** Global instance of the HTTP transport. */
	private static HttpTransport HTTP_TRANSPORT;

	/** Global instance of the scopes required by this quickstart.
	 *
	 * If modifying these scopes, delete your previously saved credentials
	 * at ~/.credentials/drive-java-quickstart
	 */
	private static final List<String> SCOPES =
			Arrays.asList(DriveScopes.DRIVE);

	static {
		try {
			HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
			DATA_STORE_FACTORY = new FileDataStoreFactory(DATA_STORE_DIR);
		} catch (Throwable t) {
			t.printStackTrace();
			System.exit(1);
		}
	}

	/**
	 * Creates an authorized Credential object.
	 * @return an authorized Credential object.
	 * @throws IOException
	 */
	public static Credential authorize() throws IOException {
		// Load client secrets.
		InputStream in =
				MainController.class.getResourceAsStream("/client_secret.json");
		GoogleClientSecrets clientSecrets =
				GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

		// Build flow and trigger user authorization request.
		GoogleAuthorizationCodeFlow flow =
				new GoogleAuthorizationCodeFlow.Builder(
						HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
		.setDataStoreFactory(DATA_STORE_FACTORY)
		.setAccessType("offline")
		.build();
		Credential credential = null;
		try {
			credential = new AuthorizationCodeInstalledApp(
					flow, new LocalServerReceiver()).authorize("user");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(
				"Credentials saved to " + DATA_STORE_DIR.getAbsolutePath());
		return credential;
	}

	/**
	 * Build and return an authorized Drive client service.
	 * @return an authorized Drive client service
	 * @throws IOException
	 */
	public static Drive getDriveService() throws IOException {
		Credential credential = authorize();
		return new Drive.Builder(
				HTTP_TRANSPORT, JSON_FACTORY, credential)
		.setApplicationName(APPLICATION_NAME)
		.build();
	}


	private StorageService storageService = null;

	@Autowired
	public MainController(StorageService storageService) {
		this.storageService = storageService;
	}

	@Autowired // tu dong ket noi
	private TinTucService tinTucService;

	@RequestMapping(value = "/getTinTuc",method = RequestMethod.POST)
	public String AddNewTinTuc(@RequestParam String txtTitle, @RequestParam String txtContent, @RequestParam("fileUpload") MultipartFile file,
			RedirectAttributes redirectAttributes, HttpServletRequest request)throws Exception{

		// Build a new authorized API client service.
		//google drive
		storageService.store(file);
		Drive service = getDriveService();

		File fileMetadata = new File();
		// Tạo một file metadata kiểu dữ liệu file bên google
		fileMetadata.setName(file.getOriginalFilename());
		// Lấy file meta này tạo thành một file mới dựa vào đường dẫn "upload-dir/"+file.getOriginalFilename()
		// file file.getOriginalFilename() chính là file multipartfile khi upload vào làm tên chính
		java.io.File filePath = new java.io.File("upload-dir/"+file.getOriginalFilename());
		FileContent mediaContent = new FileContent(file.getContentType(),filePath);// lấy kiểu dữ liệu file khi upload
		File f = service.files().create(fileMetadata, mediaContent)
				.setFields("id")
				.execute();// dịch vụ tìm tất cả các file và tạo mới file.
		TinTuc tt = new TinTuc();
		tt.setContent(txtContent);
		tt.setTitle(txtTitle);
		tt.setLink("https://drive.google.com/open?id="+f.getId());
		tinTucService.save(tt);
		request.setAttribute("tintucs", tinTucService.findAll());
		request.setAttribute("mode", "MODE_HOME");
		storageService.deleteAll();
		storageService.init();
		return "index";
	}
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String listUploadedFiles(HttpServletRequest request) throws IOException {
		request.setAttribute("mode", "MODE_HOME");
		request.setAttribute("tintucs", tinTucService.findAll());
		return "index";
	}

	@RequestMapping(value = "detail", method = RequestMethod.GET)
	public String DetailFiles(@RequestParam int id, HttpServletRequest request) throws IOException {
		int []A = new int[tinTucService.findAll().size()];
		for(int i=0;i<tinTucService.findAll().size();i++){
			A[i] = tinTucService.findAll().get(i).getId();
		}
		int j=0;
		while(A[j] != id && j<A.length){
			j++;
		}
		if(j<A.length){
			if(A.length > 1){
				request.setAttribute("mode", "MODE_DETAIL");
				request.setAttribute("tintuc", tinTucService.findId(A[j]));
				if(j>0 && j<A.length-1){
					request.setAttribute("tintucNEXT",tinTucService.findId(A[j+1]) );
					request.setAttribute("tintucPREV",tinTucService.findId(A[j-1]) );	
				}
				else if(j == A.length -1){
					request.setAttribute("tintucNEXT",tinTucService.findId(A[j]) );
					request.setAttribute("tintucPREV",tinTucService.findId(A[j-1]) );	
				}
				else if(j==0 ){
					request.setAttribute("tintucNEXT",tinTucService.findId(A[j+1]) );
					request.setAttribute("tintucPREV",tinTucService.findId(A[j]) );	
				}
			}
			if(A.length <= 1){
				request.setAttribute("mode", "MODE_DETAIL");
				request.setAttribute("tintuc", tinTucService.findId(A[j]));
				request.setAttribute("tintucNEXT",tinTucService.findId(A[j]) );
				request.setAttribute("tintucPREV",tinTucService.findId(A[j]) );	
			}

		}



		return "index";
	}

	@RequestMapping(value = "add-post", method = RequestMethod.GET)
	public String addPost(HttpServletRequest request) {
		request.setAttribute("mode", "MODE_ADD");
		return "index";
	}

	@RequestMapping(value = "delete-post", method = RequestMethod.GET)
	public String deletePost(@RequestParam int id, HttpServletRequest request) {
		try {
			//Post post = postService.findPost(id);
			TinTuc tt = tinTucService.findId(id);
			deleteFile(getDriveService(), tt.getLink().split("=")[1]);
		} catch (Exception e) {
		}
		tinTucService.delete(id);
		request.setAttribute("tintucs", tinTucService.findAll());
		request.setAttribute("mode", "MODE_HOME");
		return "redirect:/";
	}
	private static void deleteFile(Drive service, String fileId) {
		try {
			service.files().delete(fileId).execute();
		} catch (IOException e) {
			System.out.println("Delete file error: " + e);
		}
	}


	@ExceptionHandler(StorageFileNotFoundException.class)
	public ResponseEntity handleStorageFileNotFound(StorageFileNotFoundException exc) {
		return ResponseEntity.notFound().build();
	}
}
