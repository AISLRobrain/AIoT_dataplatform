package aisl.ksensor.servicemanager.common.exception;

import aisl.ksensor.servicemanager.common.code.ServiceManagerCode.ErrorCode;

public abstract class BaseException extends RuntimeException {

    private static final long serialVersionUID = 6697553987008675632L;

    ErrorCode errorCode = null;

    public BaseException( ErrorCode errorCode ) {
        this.errorCode = errorCode;
    }

    public BaseException( ErrorCode errorCode, String msg ) {
        super( msg );
        this.errorCode = errorCode;
    }

    public BaseException( ErrorCode errorCode, Throwable throwable ) {
        super( throwable );
        this.errorCode = errorCode;
    }

    public BaseException( ErrorCode errorCode, String msg, Throwable throwable ) {
        super( msg, throwable );
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode.getCode();
    }
}