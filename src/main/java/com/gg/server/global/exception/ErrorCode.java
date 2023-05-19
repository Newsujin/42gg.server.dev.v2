package com.gg.server.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ErrorCode {

    //user
    USER_NOT_FOUND(404, "UR100", "USER NOT FOUND"),

    //season
    SEASON_NOT_FOUND(404, "SE100", "SEASON NOT FOUND"),

    //rank
    RANK_NOT_FOUND(404, "RK100", "RANK NOT FOUND"),
    REDIS_RANK_NOT_FOUND(404, "RK101", "REDIS RANK NOT FOUND"),

    //authentication
    UNAUTHORIZED(401, "AU000", "UNAUTHORIZED"),


    /** Common **/
    INTERNAL_SERVER_ERR(500, "CM001","INTERNAL SERVER ERROR"),
    NOT_FOUND(404, "CM002", "NOT FOUND"),
    BAD_REQUEST(400, "CM003", "BAD REQUEST"),
    PAGE_NOT_FOUND(404, "CM004", "PAGE NOT FOUND"),
    METHOD_NOT_ALLOWED(405, "CM005", "METHOD NOT ALLOWED"),
    VALID_FAILED(400, "GAME-ERR-400" , "Valid Test Failed."),
    SN001(400, "SN001", "요청하신 값은 현 null 입니다"),
    AWS_S3_ERR(500, "CL001", "AWS S3 Error"),
    AWS_SERVER_ERR(500, "CL002", "AWS Error"),

    // SENDER
    SLACK_USER_NOT_FOUND(404, "SL001", "fail to get slack user info"),
    SLACK_CH_NOT_FOUND(404, "SL002", "fail to get user dm channel id"),
    SLACK_JSON_PARSE_ERR(400, "SL002", "json parse error"),
    SLACK_SEND_FAIL(400, "SL003","fail to send notification" )
    ;
    private int status;
    private String errCode;
    private String message;

    public void setMessage(String msg) {
        this.message = msg;
    }
}
