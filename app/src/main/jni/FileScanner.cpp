//
// Created by dingyl on 2019/11/7.
//
#include "com_ktc_media_scan_FileScannerJni.h"

#include <stdio.h>
#include <android/log.h>
#include <sys/stat.h>
#include <dirent.h>
#include <string.h>
#include <malloc.h>
#include <pthread.h>

#define LOGE(...) \
  ((void)__android_log_print(ANDROID_LOG_ERROR, "FileScanner::", __VA_ARGS__))
#define LOGD(...) \
  ((void)__android_log_print(ANDROID_LOG_DEBUG, "FileScanner::", __VA_ARGS__))

jclass baseData_cls;
jmethodID baseData_constructor;
jmethodID baseData_setFilePath;
jmethodID baseData_setFileName;
jmethodID baseData_setFileType;

jobject context;
const int PATH_MAX_LENGTH = 256;

jobject serviceObject;
jmethodID insertData;
jmethodID setFinishAction;
jint videoType;
jint musicType;
jint pictureType;
jobjectArray videoTypes;
jobjectArray musicTypes;
jobjectArray pictureTypes;

extern "C"
void JNICALL Java_com_ktc_media_scan_FileScannerJni_scanFiles
        (JNIEnv *env, jclass thiz, jstring str) {
    init(env);
    char *path = (char *) env->GetStringUTFChars(str, nullptr);
    doScanFile(env, str);
    env->CallVoidMethod(serviceObject, setFinishAction);
    env->ReleaseStringUTFChars(str, path);
}

void init(JNIEnv *env) {
    context = getGlobalContext(env);
    baseData_cls = env->FindClass(
            "com/ktc/media/model/BaseData");
    baseData_constructor = env->GetMethodID(baseData_cls, "<init>", "()V");
    baseData_setFilePath = env->GetMethodID(baseData_cls, "setPath",
                                            "(Ljava/lang/String;)V");
    baseData_setFileName = env->GetMethodID(baseData_cls, "setName",
                                            "(Ljava/lang/String;)V");
    baseData_setFileType = env->GetMethodID(baseData_cls, "setType",
                                            "(I)V");

    jclass service = env->FindClass("com/ktc/media/scan/FileScanManager");
    jmethodID serviceConstructor = env->GetMethodID(service, "<init>",
                                                    "(Landroid/content/Context;)V");
    serviceObject = env->NewObject(service,
                                   serviceConstructor, context);
    insertData = env->GetMethodID(service, "insertData",
                                  "(ILcom/ktc/media/model/BaseData;)V");
    setFinishAction = env->GetMethodID(service, "sendFinishAction",
                                       "()V");

    jfieldID video = env->GetFieldID(service, "videoType", "I");
    jfieldID music = env->GetFieldID(service, "musicType", "I");
    jfieldID picture = env->GetFieldID(service, "pictureType", "I");
    jfieldID videoArray = env->GetFieldID(service, "videoTypes", "[Ljava/lang/String;");
    jfieldID musicArray = env->GetFieldID(service, "musicTypes", "[Ljava/lang/String;");
    jfieldID pictureArray = env->GetFieldID(service, "pictureTypes", "[Ljava/lang/String;");

    videoType = env->GetIntField(serviceObject, video);
    musicType = env->GetIntField(serviceObject, music);
    pictureType = env->GetIntField(serviceObject, picture);
    videoTypes = (jobjectArray) env->GetObjectField(serviceObject, videoArray);
    musicTypes = (jobjectArray) env->GetObjectField(serviceObject, musicArray);
    pictureTypes = (jobjectArray) env->GetObjectField(serviceObject, pictureArray);
}

void doScanFile(JNIEnv *env, jstring dirPath_) {

    if (dirPath_ == nullptr) {
        LOGE("dirPath is null!");
        return;
    }
    const char *dirPath = env->GetStringUTFChars(dirPath_, nullptr);
    if (strlen(dirPath) == 0) {
        LOGE("dirPath length is 0!");
        return;
    }
    //打开文件夹读取流
    DIR *dir = opendir(dirPath);
    if (nullptr == dir) {
        LOGE("can not open dir,  check path or permission!");
        return;
    }

    struct dirent *file;
    while ((file = readdir(dir)) != nullptr) {
        //判断是不是 . 或者 .. 文件夹
        if (strcmp(file->d_name, ".") == 0 || strcmp(file->d_name, "..") == 0) {
            //LOGD("ignore . and ..");
            continue;
        }
        if (file->d_type == DT_DIR) {
            char *path = new char[PATH_MAX_LENGTH];
            memset(path, 0, PATH_MAX_LENGTH);
            strcpy(path, dirPath);
            strcat(path, "/");
            strcat(path, file->d_name);
            jstring tDir = env->NewStringUTF(path);
            doScanFile(env, tDir);
            //释放文件夹路径内存
            env->DeleteLocalRef(tDir);
            free(path);
        } else {
            insertVideoData(env, dirPath, file, videoTypes, videoType);
            insertVideoData(env, dirPath, file, musicTypes, musicType);
            insertVideoData(env, dirPath, file, pictureTypes, pictureType);
            //LOGD("%s/%s", dirPath, file->d_name);
        }
    }
    //关闭读取流
    closedir(dir);
    env->ReleaseStringUTFChars(dirPath_, dirPath);
}

void insertVideoData(JNIEnv *env, const char *dirPath, dirent *file, jobjectArray types, jint fileType) {
    int size = env->GetArrayLength(types);
    for (int i = 0; i < size; i++) {
        jstring type = (jstring) env->GetObjectArrayElement(types, i);
        jstring nameStr = env->NewStringUTF(file->d_name);
        char *typeStr = (char *) env->GetStringUTFChars(type, nullptr);
        char *name = file->d_name;
        char *extension = strrchr(name, '.');
        if (extension == nullptr
            || name == nullptr) {
            continue;
        }
        char *path = new char[PATH_MAX_LENGTH];
        memset(path, 0, PATH_MAX_LENGTH);
        strcpy(path, dirPath);
        strcat(path, "/");
        strcat(path, file->d_name);
        jstring tDir = env->NewStringUTF(path);
        bool isTargetFile = false;
        if (strcmp(typeStr, extension) == 0) {
            if (nullptr != insertData) {
                jobject baseData_obj = env->NewObject(baseData_cls,
                                                      baseData_constructor);
                env->CallVoidMethod(baseData_obj, baseData_setFilePath, tDir);
                env->CallVoidMethod(baseData_obj, baseData_setFileName, nameStr);
                env->CallVoidMethod(baseData_obj, baseData_setFileType, fileType);
                isTargetFile = true;
                env->CallVoidMethod(serviceObject, insertData, fileType, baseData_obj);
                env->DeleteLocalRef(baseData_obj);
            }
        }
        if (isTargetFile) {
            continue;
        }
        env->DeleteLocalRef(type);
        env->DeleteLocalRef(tDir);
        env->DeleteLocalRef(nameStr);
        free(path);
        env->ReleaseStringUTFChars(type, typeStr);
    }
}


jobject getGlobalContext(JNIEnv *env) {
    jclass activityThread = env->FindClass("android/app/ActivityThread");
    jmethodID currentActivityThread = env->GetStaticMethodID(activityThread,
                                                             "currentActivityThread",
                                                             "()Landroid/app/ActivityThread;");
    jobject at = env->CallStaticObjectMethod(activityThread, currentActivityThread);
    jmethodID getApplication = env->GetMethodID(activityThread, "getApplication",
                                                "()Landroid/app/Application;");
    jobject context = env->CallObjectMethod(at, getApplication);
    return context;
}