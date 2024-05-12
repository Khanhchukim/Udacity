package com.udacity.jwdnd.course1.cloudstorage.mapper;

import com.udacity.jwdnd.course1.cloudstorage.model.Credential;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CredentialMapper {
    @Insert("INSERT INTO CREDENTIALS (url, username, key, password, userid) " +
            "VALUES(#{url}, #{userName}, #{key}, #{password}, #{userId})")
    @Options(useGeneratedKeys = true, keyProperty = "credentialId")
    int insertCredential(Credential credential);

    @Select("SELECT * FROM CREDENTIALS WHERE url = #{url} AND userid = #{userId}")
    Credential selectCredentail(String url, Integer userId);

    @Select("SELECT * FROM CREDENTIALS WHERE userid = #{userId}")
    List<Credential> selectAllCredentials(int userId);

    @Delete("DELETE FROM CREDENTIALS WHERE credentialid = #{credentialId} AND userid = #{userId}")
    int deleteCredential(Integer credentialId, Integer userId);

    @Update("UPDATE CREDENTIALS SET url = #{url}, " +
            "username = #{userName}, " +
            "password = #{password}, " +
            "key = #{key} " +
            "WHERE credentialid = #{credentialId} AND userid = #{userId}")
    int updateCredential(Credential credential);
}
