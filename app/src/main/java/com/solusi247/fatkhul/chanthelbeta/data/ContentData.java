package com.solusi247.fatkhul.chanthelbeta.data;

/**
 * Created by 247 on 28/03/2018.
 */

public class ContentData {
    private String content_id,content_pid,content_name,template_id,content_ext;
    private int content_image;


    public String getId(){
        return content_id;
    }
    public void setId(String id){
        this.content_id = id;
    }
    public String getPid(){
        return content_pid;
    }
    public void setPid(String pid){
        this.content_pid = pid;
    }
    public String getName(){
        return content_name;
    }
    public void setName(String name){
        this.content_name = name;
    }
    public String getTemplate_id(){
        return template_id;
    }
    public void setTemplate_id(String template_id){
        this.template_id = template_id;
    }
    public String getExt(){
        return content_ext;
    }
    public void setExt(String ext){
        this.content_ext = ext;
    }
    public int getContent_image() {
        return content_image;
    }
    public void setContent_image(int file_image) {
        this.content_image = file_image;
    }
}
