package airtraffic.annotations;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import javax.inject.Qualifier;
import org.apache.commons.lang3.AnnotationUtils;

@Qualifier
@Retention(RUNTIME)
@Target(TYPE)
public @interface StreamStyle {
   Annotation INSTANCE = new StreamStyle() {
      @Override
      public Class<? extends Annotation> annotationType() {
         return StreamStyle.class;
      }

      @Override
      public boolean equals(Object obj) {
         return obj instanceof Annotation
            ? AnnotationUtils.equals(INSTANCE, (Annotation) obj)
            : false;
      }

      @Override
      public int hashCode() {
         return AnnotationUtils.hashCode(INSTANCE);
      }

      @Override
      public String toString() {
         return AnnotationUtils.toString(INSTANCE);
      }
   };
}