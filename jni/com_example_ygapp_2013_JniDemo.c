#include <string.h>
#include <jni.h>

jstring Java_com_example_ygapp_12013_JniDemo_stringFromJNI
  (JNIEnv *env, jobject tmp)
{
    return (*env)->NewStringUTF(env, "Hello from JNI !");
}
jstring Java_com_example_ygapp_12013_JniDemo_stringFromJNI_12
  (JNIEnv *env, jobject tmp)
{
    return (*env)->NewStringUTF(env, "Hello from JNI 2342!");
}
jint  Java_com_example_ygapp_12013_JniDemo_add
  (JNIEnv *env, jobject tmp, jint x, jint y)
{
    return (x+y);
}