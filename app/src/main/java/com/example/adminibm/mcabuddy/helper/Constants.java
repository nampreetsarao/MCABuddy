package com.example.adminibm.mcabuddy.helper;

/**
 * Created by nampreet on 5/2/2016.
 */
public final class Constants {

    //Bluemix
    public static String baseURL="http://169.44.9.228:8080/mcabuddy";
    //AWS
    //public static String baseURL="http://52.221.244.83:8080/mcabuddy";
    public static String createUserURL;
    public static String authenticateURL="/user/authenticate";
    public static String getMessageForAThreadURLFirstPart="/channels";
    public static String getMessageForAThreadURLSecondPart="/messages/today?index=0";
    public static String broadcast="/broadcast";
    public static String information="/information";
    public static String sos="/sos";
    public static String knowledge="/knowledge";
    public static String changeRolePart1="/user/";
    public static String changeRolePart2="/role/";
    public static String newUserURL="/user/new";
    public static String addExpertise="/expertise/";
    public static String likeMessagePart1="/channels/messages/";
    public static String likeMessagePart2="/like";
    public static String tagMessagePart1="/channels/messages/";
    public static String tagMessagePart2="/tag/";
    public static String searchUserByAny="/user?any=";
    public static String postNewMessagePart1="/channels/";
    public static String postNewMessagePart2="/message/new";
    public static String replyToMessagePart1="/channels/";
    public static String replyToMessagePart2="/message/";
    public static String replyToMessagePart3="/reply";
    public static String fetchMessageForAThread="/channels/messages/thread/";
    public static String forPagination="?index=0";




}
