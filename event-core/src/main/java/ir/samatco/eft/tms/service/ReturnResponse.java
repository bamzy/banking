package ir.samatco.eft.tms.service;

import java.nio.ByteBuffer;

/**
 * Created by Bamdad Aghili on 5/7/2014.
 */
public class ReturnResponse {
    private ByteBuffer byteBuffer;
    private boolean isError = false;

    public ByteBuffer getByteBuffer() {
        return byteBuffer;
    }

    public void setByteBuffer(ByteBuffer byteBuffer) {
        this.byteBuffer = byteBuffer;
    }

    public boolean isError() {
        return isError;
    }

    public void setError(boolean isError) {
        this.isError = isError;
    }
}
