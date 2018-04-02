package cn.churen.biz.util;

import java.net.URL;
import java.net.URLClassLoader;

public class AJavaUtil {

  public static URL[] queryClassPath() {
    URL[] urls = ((URLClassLoader) (ClassLoader.getSystemClassLoader())).getURLs();
    for(URL url: urls){
      System.out.println(url.getFile());
    }
    return urls;
  }
}
