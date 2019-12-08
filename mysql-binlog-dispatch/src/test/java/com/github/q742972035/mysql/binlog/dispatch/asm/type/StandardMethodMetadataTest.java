package com.github.q742972035.mysql.binlog.dispatch.asm.type;


import com.github.q742972035.mysql.binlog.dispatch.annotation.Column;
import com.github.q742972035.mysql.binlog.dispatch.asm.core.LocalVariableTableParameterNameDiscoverer;
import com.github.q742972035.mysql.binlog.dispatch.asm.core.ParameterNameDiscoverer;
import com.github.q742972035.mysql.binlog.dispatch.scan.MethodMetadata;
import com.github.q742972035.mysql.binlog.dispatch.scan.MethodParameter;
import com.github.q742972035.mysql.binlog.dispatch.scan.SimpleMethodMetadata;
import com.github.q742972035.mysql.binlog.dispatch.utils.PrintUtils;
import org.assertj.core.api.Assertions;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.annotation.*;
import java.lang.reflect.Method;
import java.util.List;

public class StandardMethodMetadataTest extends AbstractMethodMetadataTests{

    @Override
    protected AnnotationMetadata get(Class<?> source) {
        return AnnotationMetadata.introspect(source);
    }

    ParameterNameDiscoverer parameterNameDiscoverer = new LocalVariableTableParameterNameDiscoverer();

    @Test
    public void testGetMethodParamaters() throws NoSuchMethodException {
        Method method = GetMethodParamaters.class.getMethod("getMe", int.class, String.class, Object.class);
        String[] parameterNames = parameterNameDiscoverer.getParameterNames(method);
        Assertions.assertThat(PrintUtils.print(parameterNames)).isEqualTo("a,b,c");
    }

    @Test
    public void testStandardMethodMetadata() throws NoSuchMethodException {
        Method myUser = AnnotationMethodMetadata.class.getDeclaredMethod("myUser", String.class, String.class);
        com.github.q742972035.mysql.binlog.dispatch.scan.MethodMetadata  methodMetadata = new SimpleMethodMetadata(myUser);

        assertThat(methodMetadata.getReturnType()).isEqualTo(User.class);
        assertThat(methodMetadata.getReturnType().getName()).isEqualTo("zy.opsource.dispath.asm.type.StandardMethodMetadataTest$User");

        List<Annotation> declaredAnnotations = methodMetadata.getDeclaredAnnotations();
        assertThat(declaredAnnotations.size()).isEqualTo(1);
        assertThat(declaredAnnotations.get(0).toString()).isEqualTo("@zy.opsource.dispath.asm.type.StandardMethodMetadataTest$MethodAnnotation()");


        List<MethodParameter> methodParameters = methodMetadata.getMethodParameters();
        assertThat(methodParameters.size()).isEqualTo(2);

        assertThat(methodParameters.get(0).getClazz()).isEqualTo(String.class);
        assertThat(methodParameters.get(0).getName()).isEqualTo("id");

        assertThat(methodParameters.get(1).getClazz()).isEqualTo(String.class);
        assertThat(methodParameters.get(1).getName()).isEqualTo("name");

    }

    // 自测缓存
    @Test
    public void testCache() throws NoSuchMethodException {

        Method myUser = AnnotationMethodMetadata.class.getDeclaredMethod("myUser", String.class, String.class);
        MethodMetadata methodMetadata = new SimpleMethodMetadata(myUser);

        List<Annotation> declaredAnnotations1 = methodMetadata.getDeclaredAnnotations();
        List<Annotation> declaredAnnotations2 = methodMetadata.getDeclaredAnnotations();

        assertThat(declaredAnnotations1).isEqualTo(declaredAnnotations2);
    }




    public static class User{

    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.METHOD})
    public static  @interface MethodAnnotation{

    }

    public static class AnnotationMethodMetadata{

        @MethodAnnotation
        public User myUser(String id,@Column("name") String ss){
            return new User();
        }


    }




    public static class GetMethodParamaters{


        public String getMe(int a,String b,Object c){
            return null;
        }

    }
}