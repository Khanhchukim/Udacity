package com.udacity.jwdnd.course1.cloudstorage.mapper;

import com.udacity.jwdnd.course1.cloudstorage.model.File;
import com.udacity.jwdnd.course1.cloudstorage.model.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface FileMapper {
    @Insert("INSERT INTO FILES (filename, contenttype, filesize, userid, filedata)" +
            "VALUES(#{fileName}, #{contentType}, #{fileSize}, #{userId}, #{fileData})")
    @Options(useGeneratedKeys = true, keyProperty = "fileId")
    int insertFile(File file);

    @Select("SELECT * FROM FILES WHERE filename = #{fileName} AND userid = #{userId}")
    File selectFile(String fileName, Integer userId);

    @Select("SELECT * FROM FILES WHERE userid = #{userId}")
    List<File> selectAllFile(Integer userId);

    @Delete("DELETE FROM FILES WHERE filename = #{fileName} AND userid = #{userId}")
    int deleteFile(String fileName, Integer userId);
}
