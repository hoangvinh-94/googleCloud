package hello.dao;

import org.springframework.data.repository.CrudRepository;

import hello.model.TinTuc;

public interface TinTucRepository extends CrudRepository<TinTuc, Integer>{ 

}
