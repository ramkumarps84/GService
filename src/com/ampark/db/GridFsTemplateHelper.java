package com.ampark.db;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;

import com.mongodb.Mongo;
import com.mongodb.MongoClient;

@Configuration
@PropertySource("classpath:application.db.properties")
public class GridFsTemplateHelper extends AbstractMongoConfiguration{
	
	Map<String, GridFsTemplate> templatesMap = new HashMap<String, GridFsTemplate>();
	
	private Environment env;
		
	@Override
	protected String getDatabaseName() {
		return env.getRequiredProperty("mongo.database", String.class);
	}

	@Override
	public Mongo mongo() throws Exception {
		return new MongoClient(env.getRequiredProperty("mongo.client", String.class));
	}
	
	public GridFsTemplate getGridFsTemplate(String bucket) throws Exception  {
		
		if(templatesMap.containsKey(bucket)){
			return templatesMap.get(bucket);
		}else{
			GridFsTemplate tem = new GridFsTemplate(mongoDbFactory(), mappingMongoConverter(),bucket);
			templatesMap.put(bucket, tem);
			return tem;
		}
	}
	
	public Environment getEnv() {
		return env;
	}

	@Autowired
	public void setEnv(Environment env) {
		this.env = env;
	}

}
