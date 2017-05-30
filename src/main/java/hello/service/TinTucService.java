package hello.service;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import hello.dao.TinTucRepository;
import hello.model.TinTuc;


@Service
@Transactional
public class TinTucService {

	private final TinTucRepository tinTucRepositor;

	public TinTucService(TinTucRepository tinTucRepositor) {
		super();
		this.tinTucRepositor = tinTucRepositor;
	}
	public List<TinTuc> findAll(){
		List<TinTuc> tintucs = new ArrayList<TinTuc>();
		for(TinTuc tt: tinTucRepositor.findAll()){
			tintucs.add(tt);
		}
		return tintucs;
	}
	public void delete(int id){
		tinTucRepositor.delete(id);
	}
	public TinTuc findId(int id){
		return tinTucRepositor.findOne(id);
	}
	public void save(TinTuc tintuc){
		tinTucRepositor.save(tintuc);
	}
}
