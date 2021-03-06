package com.weibo.utils;

public final class ConstantUtil {
	public static final int TOMCAT_PORT = 8080;
	public static final int PORT = 8989;
	public static int user_id = 0;
//	 public static final String ADDRESS = "192.168.1.180";
	//不能在前后有空格
	public static final String ADDRESS = "192.168.253.1";
	public static final String ROOTDIR = "http://" + ADDRESS + ":"
			+ TOMCAT_PORT + "/JBlog/";
	public static final boolean DEBUG = true;
	public static final String LOGIN_SUCCESS = "<#LOGIN_SUCCESS#>";
	public static final String LOGIN_FAIL = "<#LOGIN_FAIL#>";
	public static final String LOGIN = "<#LOGIN#>";
	public static final String UPLOAD = "<#UPLOAD#>";
	public static final String UPLOAD_SUCCESS = "<#UPLOAD_SUCCESS#>";
	public static final String UPLOAD_FAIL = "<#UPLOAD_FAIL#>";
	public static final String REGISTER = "<#REGISTER#>";
	public static final String REGISTER_SUCCESS = "<#REGISTER_SUCCESS#>";
	public static final String REGISTER_FAIL = "<#REGISTER_FAIL#>";
	public static final String PUBLISH = "<#PUBLISH#>";
	public static final String PUBLISH_SUCCESS = "<#PUBLISH_SUCCESS#>";
	public static final String PUBLISH_FAIL = "<#PUBLISH_FAIL#>";
	public static final String DIARYLIST = "<#DIARYLIST#>";
	public static final String DIARYLIST_SUCCESS = "<#DIARYLIST_SUCCESS#>";
	public static final String DIARYLIST_FAIL = "<#DIARYLIST_FAIL#>";
	public static final String DIARY = "<#DIARY#>";
	public static final String DIARY_SUCCESS = "<#DIARY_SUCCESS#>";
	public static final String DIARY_FAIL = "<#DIARY_FAIL#>";
	public static final String ALBUM = "<#ALBUM#>";
	public static final String ALBUM_SUCCESS = "<#ALBUM_SUCCESS#>";
	public static final String ALBUM_FAIL = "<#ALBUM_FAIL#>";
	public static final String ALBUMLIST = "<#ALBUMLIST#>";
	public static final String ALBUMLIST_SUCCESS = "<#ALBUMLIST_SUCCESS#>";
	public static final String ALBUMLIST_FAIL = "<#ALBUMLIST_FAIL#>";
	public static final String VISITORLIST = "<#VISITORLIST#>";
	public static final String VISITORLIST_SUCCESS = "<#VISITORLIST_SUCCESS#>";
	public static final String VISITORLIST_FAIL = "<#VISITORLIST_FAIL#>";
	public static final String USERINFO = "<#USERINFO#>";
	public static final String USERINFO_SUCCESS = "<#USERINFO_SUCCESS#>";
	public static final String USERINFO_FAIL = "<#USERINFO_FAIL#>";
	public static final String DELETEDIARY = "<#DELETEDIARY#>";
	public static final String DELETEDIARY_SUCCESS = "<#DELETEDIARY_SUCCESS#>";
	public static final String DELETEDIARY_FAIL = "<#DELETEDIARY_FAIL#>";
	public static final String DELETEPHOTO = "<#DELETEPHOTO#>";
	public static final String DELETEPHOTO_SUCCESS = "<#DELETEPHOTO_SUCCESS#>";
	public static final String DELETEPHOTO_FAIL = "<#DELETEPHOTO_FAIL#>";
	public static final String GETPHOTO = "<#GETPHOTO#>";
	public static final String GETPHOTO_SUCCESS = "<#GETPHOTO_SUCCESS#>";
	public static final String GETPHOTO_FAIL = "<#GETPHOTO_FAIL#>";
	public static final String GETHEAD = "<#GETHEAD#>";
	public static final String GETHEAD_SUCCESS = "<#GETHEAD_SUCCESS#>";
	public static final String GETHEAD_FAIL = "<#GETHEAD_FAIL#>";
	public static final String GETHOME = "<#GETHOME#>";
	public static final String GETHOME_SUCCESS = "<#GETHOME_SUCCESS#>";
	public static final String GETHOME_FAIL = "<#GETHOME_FAIL#>";
	public static final String GETCOMMENT = "<#GETCOMMENT#>";
	public static final String GETCOMMENT_SUCCESS = "<#GETCOMMENT_SUCCESS#>";
	public static final String GETCOMMENT_FAIL = "<#GETCOMMENT_FAIL#>";
	public static final String SAVECOMMENT = "<#SAVECOMMENT#>";
	public static final String SAVECOMMENT_SUCCESS = "<#SAVECOMMENT_SUCCESS#>";
	public static final String SAVECOMMENT_FAIL = "<#SAVECOMMENT_FAIL#>";
	public static final String ADDLAUD="<#ADDLAUD#>";
	public static final String ADDLAUD_SUCCESS="<#ADDLAUD_SUCCESS#>";
	public static final String ADDLAUD_FAIL="<#ADDLAUD_FAIL#>";
	public static final String ADDTRANSMIT="<#ADDTRANSMIT#>";
	public static final String ADDTRANSMIT_SUCCESS="<#ADDTRANSMIT_SUCCESS#>";
	public static final String ADDTRANSMIT_FAIL="<#ADDTRANSMIT_FAIL#>";
	public static final String GETHOT = "<#GETHOT#>";
	public static final String GETHOT_SUCCESS = "<#GETHOT_SUCCESS#>";
	public static final String GETHOT_FAIL = "<#GETHOT_FAIL#>";
	public static final String SETATTENTION = "<#SETATTENTION#>";
	public static final String SETATTENTION_SUCCESS = "<#SETATTENTION_SUCCESS#>";
	public static final String SETATTENTION_FAIL = "<#SETATTENTION_FAIL#>";
	public static final String CANCELATTENTION = "<#CANCELATTENTION#>";
	public static final String CANCELATTENTION_SUCCESS = "<#CANCELATTENTION_SUCCESS#>";
	public static final String CANCELATTENTION_FAIL = "<#CANCELATTENTION_FAIL#>";
	public static final String GETATTENTION = "<#GETATTENTION#>";
	public static final String GETATTENTION_SUCCESS = "<#GETATTENTION_SUCCESS#>";
	public static final String GETATTENTION_FAIL = "<#GETATTENTION_FAIL#>";
	public static final String SETCOLLECTION = "<#SETCOLLECTION#>";
	public static final String SETCOLLECTION_SUCCESS = "<#SETCOLLECTION_SUCCESS#>";
	public static final String SETCOLLECTION_FAIL = "<#SETCOLLECTION_FAIL#>";
	public static final String CANCELCOLLECTION = "<#CANCELCOLLECTION#>";
	public static final String CANCELCOLLECTION_SUCCESS = "<#CANCELCOLLECTION_SUCCESS#>";
	public static final String CANCELCOLLECTION_FAIL = "<#CANCELCOLLECTION_FAIL#>";
	public static final String GETCOLLECTION = "<#GETCOLLECTION#>";
	public static final String GETCOLLECTION_SUCCESS = "<#GETCOLLECTION_SUCCESS#>";
	public static final String GETCOLLECTION_FAIL = "<#GETCOLLECTION_FAIL#>";
	public static final String GETFANS = "<#GETFANS#>";
	public static final String GETFANS_SUCCESS = "<#GETFANS_SUCCESS#>";
	public static final String GETFANS_FAIL = "<#GETFANS_FAIL#>";
	public static final String SEARCHUSER = "<#SEARCHUSER#>";
	public static final String SEARCHUSER_SUCCESS = "<#SEARCHUSER_SUCCESS#>";
	public static final String SEARCHUSER_FAIL = "<#SEARCHUSER_FAIL#>";
	public static final String SEARCHDIARY = "<#SEARCHDIARY#>";
	public static final String SEARCHDIARY_SUCCESS = "<#SEARCHDIARY_SUCCESS#>";
	public static final String SEARCHDIARY_FAIL = "<#SEARCHDIARY_FAIL#>";
	public static final String ALTERUSERINFO = "<#ALTERUSERINFO#>";
	public static final String ALTERUSERINFO_SUCCESS = "<#ALTERUSERINFO_SUCCESS#>";
	public static final String ALTERUSERINFO_FAIL = "<#ALTERUSERINFO_FAIL#>";
}
