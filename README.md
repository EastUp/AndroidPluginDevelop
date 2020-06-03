@[TOC](Android插件化)

**插件化主要分为两大类**
1. 资源插件化
2. Activity的插件化（难在绕过AndroidMnifest的检测）

## 资源插件化

需要先了解
1. [资源加载的源码](1.插件式换肤-资源加载源码分析.md)
2. [AppCompat以及LayoutInflate源码](2.AppCompat以及LayoutInflate源码-Hook拦截View创建.md)

最后请看`resourceplugin`这个Module的插件换肤示例

## Activity的插件化

需要先了解[Activity的启动流程](3.Activity的启动流程源码.md)

最后请看`app`这个Module的示例




 


      
     
 

