package aisl.ksensor.servicemanager.common.exception;

import aisl.ksensor.servicemanager.common.code.ServiceManagerCode;

public class HttpException extends BaseException{
    private static final long serialVersionUID = 15156165165165165L;

    public HttpException( ServiceManagerCode.ErrorCode errorCode ) {
        super( errorCode );
    }

    public HttpException(ServiceManagerCode.ErrorCode errorCode, String msg ) {
        super( errorCode, msg );
        this.errorCode = errorCode;
    }

    public HttpException(ServiceManagerCode.ErrorCode errorCode, Throwable throwable ) {
        super( errorCode, throwable );
        this.errorCode = errorCode;
    }

    public HttpException(ServiceManagerCode.ErrorCode errorCode, String msg, Throwable throwable ) {
        super( errorCode, msg, throwable );
        this.errorCode = errorCode;
    }
}
