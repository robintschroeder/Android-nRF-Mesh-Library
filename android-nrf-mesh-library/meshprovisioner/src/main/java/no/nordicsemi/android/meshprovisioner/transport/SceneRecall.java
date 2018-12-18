package no.nordicsemi.android.meshprovisioner.transport;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import no.nordicsemi.android.meshprovisioner.opcodes.ApplicationMessageOpCodes;
import no.nordicsemi.android.meshprovisioner.utils.SecureUtils;

/**
 * To be used as a wrapper class when creating a SceneStore message.
 */
@SuppressWarnings("unused")
public class SceneRecall extends GenericMessage {

    private static final String TAG = SceneRecall.class.getSimpleName();
    private static final int OP_CODE = ApplicationMessageOpCodes.SCENE_RECALL;
    private static final int SCENE_RECALL_TRANSITION_PARAMS_LENGTH = 5;
    private static final int SCENE_RECALL_PARAMS_LENGTH = 3;

    private final Integer mTransitionSteps;
    private final Integer mTransitionResolution;
    private final Integer mDelay;
    private final int mSceneNumber;
    private final int tId;

    /**
     * Constructs SceneStore message.
     *
     * @param appKey      application key for this message
     * @param sceneNumber       sceneNumber
     * @param tId                   transaction Id
     * @param aszmic      size of message integrity check
     * @throws IllegalArgumentException if any illegal arguments are passed
     */
    public SceneRecall(@NonNull final byte[] appKey,
                       final int sceneNumber,
                       final int tId,
                       final int aszmic) throws IllegalArgumentException {
        this(appKey, null, null, null, sceneNumber, tId, aszmic);
    }

    /**
     * Constructs SceneStore message.
     *
     * @param appKey               application key for this message
     * @param transitionSteps      transition steps for the level
     * @param transitionResolution transition resolution for the level
     * @param delay                delay for this message to be executed 0 - 1275 milliseconds
     * @param sceneNumber                sceneNumber
     * @param tId                   transaction Id
     * @param aszmic               size of message integrity check
     * @throws IllegalArgumentException if any illegal arguments are passed
     */
    @SuppressWarnings("WeakerAccess")
    public SceneRecall(@NonNull final byte[] appKey,
                       @Nullable final Integer transitionSteps,
                       @Nullable final Integer transitionResolution,
                       @Nullable final Integer delay,
                       final int sceneNumber,
                       final int tId,
                       final int aszmic) {
        super(appKey, aszmic);
        this.mTransitionSteps = transitionSteps;
        this.mTransitionResolution = transitionResolution;
        this.mDelay = delay;
        this.mSceneNumber = sceneNumber;
        this.tId = tId;
        assembleMessageParameters();
    }

    @Override
    public int getOpCode() {
        return OP_CODE;
    }

    @Override
    void assembleMessageParameters() {
        mAid = SecureUtils.calculateK4(mAppKey);
        final ByteBuffer paramsBuffer;
        Log.v(TAG, "Scene number: " + mSceneNumber);
        if (mTransitionSteps == null || mTransitionResolution == null || mDelay == null) {
            paramsBuffer = ByteBuffer.allocate(SCENE_RECALL_PARAMS_LENGTH).order(ByteOrder.LITTLE_ENDIAN);
            paramsBuffer.putShort((short) mSceneNumber);
            paramsBuffer.put((byte) tId);
        } else {
            Log.v(TAG, "Transition steps: " + mTransitionSteps);
            Log.v(TAG, "Transition step resolution: " + mTransitionResolution);
            paramsBuffer = ByteBuffer.allocate(SCENE_RECALL_TRANSITION_PARAMS_LENGTH).order(ByteOrder.LITTLE_ENDIAN);
            paramsBuffer.putShort((short) mSceneNumber);
            paramsBuffer.put((byte) tId);
            paramsBuffer.put((byte) (mTransitionResolution << 6 | mTransitionSteps));
            final int delay = mDelay;
            paramsBuffer.put((byte) delay);
        }
        mParameters = paramsBuffer.array();
    }
}
