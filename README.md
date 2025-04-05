# java-hpack-demo

#### 介绍
java中使用类似hpack静态表对http headers进行json压缩的一个简单demo, 主要是能够兼顾压缩以后的阅读

原始长度：702

原始zstd压缩长度：700

原始gzip压缩长度：680

HPACK长度：682, 提升压缩率2.85%

HPACK and zstd压缩长度：672, 提升压缩率4%

HPACK and gzip压缩长度：656, 提升压缩率3.53%

#### 软件架构
软件架构说明
java-hpack-util - 压缩代码，无任何依赖

java-hpack-test - web测试类，测试压缩浏览器提交的header

#### 安装教程

1.  xxxx
2.  xxxx
3.  xxxx

#### 使用说明

1.  xxxx
2.  xxxx
3.  xxxx

#### 参与贡献

1.  Fork 本仓库
2.  新建 Feat_xxx 分支
3.  提交代码
4.  新建 Pull Request

