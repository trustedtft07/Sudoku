#include <jni.h>
#include "sudoku_engine.hpp"

namespace {
SudokuEngine& engine() {
    static SudokuEngine instance;
    return instance;
}
}

extern "C" JNIEXPORT void JNICALL
Java_com_sudoku_ndk_SudokuNative_newGame(JNIEnv*, jobject, jint holes) {
    engine().newGame(static_cast<int>(holes));
}

extern "C" JNIEXPORT jboolean JNICALL
Java_com_sudoku_ndk_SudokuNative_setCell(JNIEnv*, jobject, jint row, jint col, jint value) {
    return engine().setCell(static_cast<int>(row), static_cast<int>(col), static_cast<int>(value))
        ? JNI_TRUE
        : JNI_FALSE;
}

extern "C" JNIEXPORT jboolean JNICALL
Java_com_sudoku_ndk_SudokuNative_applyHint(JNIEnv*, jobject) {
    return engine().applyHint() ? JNI_TRUE : JNI_FALSE;
}

extern "C" JNIEXPORT jboolean JNICALL
Java_com_sudoku_ndk_SudokuNative_isSolved(JNIEnv*, jobject) {
    return engine().isSolved() ? JNI_TRUE : JNI_FALSE;
}

extern "C" JNIEXPORT jboolean JNICALL
Java_com_sudoku_ndk_SudokuNative_isConflict(JNIEnv*, jobject, jint row, jint col) {
    return engine().isConflict(static_cast<int>(row), static_cast<int>(col))
        ? JNI_TRUE
        : JNI_FALSE;
}

extern "C" JNIEXPORT jintArray JNICALL
Java_com_sudoku_ndk_SudokuNative_getBoard(JNIEnv* env, jobject) {
    const auto& board = engine().board();
    jintArray result = env->NewIntArray(SudokuEngine::kSize * SudokuEngine::kSize);
    env->SetIntArrayRegion(result, 0, SudokuEngine::kSize * SudokuEngine::kSize, board.data());
    return result;
}

extern "C" JNIEXPORT jintArray JNICALL
Java_com_sudoku_ndk_SudokuNative_getFixedMask(JNIEnv* env, jobject) {
    const auto& mask = engine().fixedMask();
    jintArray result = env->NewIntArray(SudokuEngine::kSize * SudokuEngine::kSize);
    env->SetIntArrayRegion(result, 0, SudokuEngine::kSize * SudokuEngine::kSize, mask.data());
    return result;
}
