package org.fmod;

import android.media.AudioTrack;
import android.util.Log;
import java.nio.ByteBuffer;

/* loaded from: classes.dex */
public class FMODAudioDevice implements Runnable {
    private static int FMOD_INFO_SAMPLERATE = 0;
    private static int FMOD_INFO_DSPBUFFERLENGTH = 1;
    private static int FMOD_INFO_DSPNUMBUFFERS = 2;
    private static int FMOD_INFO_MIXERRUNNING = 3;
    private static int FMOD_INFO_CHANNELCOUNT = 4;
    private Thread mThread = null;
    private AudioTrack mTrack = null;
    private boolean mRunning = false;
    private boolean mInitialised = false;

    private native int fmodGetInfo(int i);

    private native int fmodProcess(ByteBuffer byteBuffer);

    public boolean isMixing() {
        return this.mInitialised;
    }

    public void start() {
        if (this.mThread != null) {
            stop();
        }
        this.mThread = new Thread(this);
        this.mThread.setPriority(10);
        this.mRunning = true;
        this.mThread.start();
    }

    public void stop() {
        while (this.mThread != null) {
            this.mRunning = false;
            try {
                this.mThread.join();
                this.mThread = null;
            } catch (InterruptedException e) {
            }
        }
    }

    @Override // java.lang.Runnable
    public void run() {
        ByteBuffer byteBuffer;
        int i;
        int i2 = 3;
        byte[] bArr = null;
        ByteBuffer byteBuffer2 = null;
        while (this.mRunning) {
            if (!this.mInitialised) {
                int iFmodGetInfo = fmodGetInfo(FMOD_INFO_SAMPLERATE);
                int iFmodGetInfo2 = fmodGetInfo(FMOD_INFO_DSPBUFFERLENGTH);
                int iFmodGetInfo3 = fmodGetInfo(FMOD_INFO_DSPNUMBUFFERS);
                int iFmodGetInfo4 = fmodGetInfo(FMOD_INFO_CHANNELCOUNT);
                if (iFmodGetInfo > 0 && iFmodGetInfo2 > 0 && iFmodGetInfo3 > 0 && iFmodGetInfo4 > 0) {
                    int iFetchChannelConfigFromCount = fetchChannelConfigFromCount(iFmodGetInfo4);
                    int minBufferSize = AudioTrack.getMinBufferSize(iFmodGetInfo, iFetchChannelConfigFromCount, 2);
                    if (minBufferSize < 0) {
                        Log.w("fmod", "FMOD: AudioDevice::run               : Couldn't query minimum buffer size, possibly unsupported sample rate or channel count");
                    } else {
                        Log.i("fmod", "FMOD: AudioDevice::run               : Min buffer size: " + minBufferSize + " bytes");
                    }
                    int i3 = iFmodGetInfo3 * iFmodGetInfo2 * iFmodGetInfo4 * 2;
                    if (i3 <= minBufferSize) {
                        i3 = minBufferSize;
                    }
                    Log.i("fmod", "FMOD: AudioDevice::run               : Actual buffer size: " + i3 + " bytes");
                    ByteBuffer byteBufferAllocateDirect = ByteBuffer.allocateDirect(iFmodGetInfo2 * iFmodGetInfo4 * 2);
                    byte[] bArr2 = new byte[byteBufferAllocateDirect.capacity()];
                    this.mTrack = new AudioTrack(3, iFmodGetInfo, iFetchChannelConfigFromCount, 2, i3, 1);
                    if (this.mTrack.getState() == 1) {
                        this.mTrack.play();
                        this.mInitialised = true;
                        i = i2;
                    } else {
                        this.mTrack.release();
                        this.mTrack = null;
                        i = i2 - 1;
                        if (i == 0) {
                            Log.e("fmod", "FMOD: AudioDevice::run               : Couldn't initialize AudioTrack, giving up");
                            this.mRunning = false;
                        } else {
                            Log.w("fmod", "FMOD: AudioDevice::run               : Couldn't initialize AudioTrack, retrying...");
                            try {
                                Thread.sleep(1000L);
                            } catch (InterruptedException e) {
                            }
                        }
                    }
                    i2 = i;
                    byteBuffer = byteBufferAllocateDirect;
                    bArr = bArr2;
                } else {
                    try {
                        Thread.sleep(100L);
                        byteBuffer = byteBuffer2;
                    } catch (InterruptedException e2) {
                        byteBuffer = byteBuffer2;
                    }
                }
                byteBuffer2 = byteBuffer;
            } else if (fmodGetInfo(FMOD_INFO_MIXERRUNNING) == 1) {
                fmodProcess(byteBuffer2);
                byteBuffer2.get(bArr);
                byteBuffer2.position(0);
                this.mTrack.write(bArr, 0, byteBuffer2.capacity());
            } else {
                shutDown();
            }
        }
        shutDown();
    }

    private void shutDown() throws IllegalStateException {
        if (this.mTrack != null) {
            this.mTrack.stop();
            this.mTrack.release();
            this.mTrack = null;
        }
        this.mInitialised = false;
    }

    private int fetchChannelConfigFromCount(int i) {
        if (i == 1) {
            return 2;
        }
        if (i == 2) {
            return 3;
        }
        return i == 6 ? 252 : 0;
    }
}
