@[TOC](Android插件化)

**插件化主要分为两大类**
1. 资源插件化
2. Activity的插件化（难在绕过AndroidMnifest的检测）

## 资源插件化

需要先了解
1. [资源加载的源码](1.插件式换肤-资源加载源码分析.md)
2. [AppCompat以及LayoutInflate源码](2.AppCompat以及LayoutInflate源码-Hook拦截View创建.md)

最后请看`resourceplugin`这个Module的插件换肤示例

## Activity、service、broadcast、contentprovider的插件化

需要先了解[Activity的启动流程](3.Activity的启动流程源码.md)

`注意`：
- 占坑的Activity的theme和插件的Activity的theme必须一致,如果没设置请确保宿主和插件application的theme一致
- 最好采用hook的这种方式,反射和手动调用会存在View无法找到的问题

最后请看`app`这个Module的示例

遇到的坑：`插件布局文件中ImageView的src资源问题`

以下代码在反射添加资源时一定要加入，`原因是因为ImageView这些是通过getTheme去查找的资源所以需要替换掉`
```
            // copy theme
            //注意：占坑的Activity的theme和插件的Activity的theme必须一致,
            // 如果没设置请确保宿主和插件application的theme一致
            Resources.Theme theme = pluginContext.getResources().newTheme();
            theme.setTo(activity.getTheme());
            int themeResource = reflect.get("mThemeResource");
            theme.applyStyle(themeResource, true);
            reflect.set("mTheme",theme);
```





 


      
     
 

