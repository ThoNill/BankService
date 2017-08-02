package fehlerManagement;

import javax.transaction.Transactional;

public class RollbackException extends RuntimeException {
    Runnable runable;
    
    public RollbackException(Runnable r) {
        super();
        this.runable = r;
    }

    public RollbackException(Runnable r,String message, Throwable cause,
            boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.runable = r;
    }

    public RollbackException(Runnable r,String message, Throwable cause) {
        super(message, cause);
        this.runable = r;
    }

    public RollbackException(Runnable r,String message) {
        super(message);
        this.runable = r;
    }

    public RollbackException(Runnable r,Throwable cause) {
        super(cause);
        this.runable = r;
    }

    @Transactional
    public void  rollback() {
        runable.run();
    }
}
