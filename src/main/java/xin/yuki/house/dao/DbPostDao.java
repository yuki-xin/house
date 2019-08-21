package xin.yuki.house.dao;

import org.springframework.data.mongodb.repository.MongoRepository;
import xin.yuki.house.model.DbPost;

public interface DbPostDao extends MongoRepository<DbPost,String> {

}
