# wsilk

wsilk is a framework that assists developers to generate code through meta info (java AST、text、db、file... ), you can automatically generate a large number of repeated logic code, also can  generate auxiliary tool classes to simplify complex problems、 improve developers' development efficiency、 standardize、improve code quality.

---
 wsilk是一个基于原信息（抽象语法树、文本、数据库信息、文件等等）辅助开发人员写代码的工具，通过这个工具，可以自动生成大量逻辑重复的代码，还能够生成辅助工具类，把复杂问题简单化,从而提升开发人员的开发效率、统一规范、提升代码质量


# 环境要求
- **JDK版本：**  支持jdk8, 
- **MAVEN ：**  maven 3.6 以上
- **开发工具：** 不限制
- **操作系统：** 不限制
  

## 更新

    最新版本 1.0.0

## MAVEN plugin 配置
 在maven pom的 plugins中新增wsilk-maven-plugin
```
<plugin>
 <groupId>com.wuba</groupId>
 <artifactId>wsilk-maven-plugin</artifactId>
 <version>1.0.0</version>
 <executions>
  <execution>
   <goals>
    <goal>process</goal>
   </goals>
  </execution>
 </executions>
 <configuration>
  <outputDirectory>wsilk/java</outputDirectory>
  <incremental>true</incremental>
  <override>true</override>
  <options>
   <logable>false</logable>
  </options>
 </configuration>
 <dependencies>
  <dependency>
   <groupId>com.wuba</groupId>
   <artifactId>wsilk-core</artifactId>
   <version>1.0.0</version>
  </dependency>
  <dependency>
   <groupId>com.wuba</groupId>
   <artifactId>wsilk-producer</artifactId>
   <version>1.0.0</version>
  </dependency>
 </dependencies>
</plugin>
```

## MAVEN dependency 配置
在maven项目中的 dependencys 中添加依赖
```
<dependency>
 <groupId>com.wuba</groupId>
 <artifactId>wsilk-producer</artifactId>
 <version>1.0.0</version>
 <scope>provided</scope>
</dependency>

```
***scope 设置为provided，这样这个jar包只在编译阶段有效，这样避免jar包对你项目的污染***

## 提示
***假如maven中提示找不到tool.jar，你可以从jdk8中找到tool.jar，并拷贝到提示寻找的目录***

## 简单使用

 1. 创建一个maven 项目
 2. 创建一个java对象，并
 ```
import java.util.Date;
import com.wuba.wsilk.producer.builder.Builder;
import com.wuba.wsilk.producer.wrapper.Wrapper;

@Wrapper
@Builder
public class User {
	private String id;
	private String username;
	private String password;
	private Date createTime;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
}
```
 3. 调用  mvn generate-sources
 4. 观察wsilk/java下是否生成了相应代码
 
## 更多介绍
   我们将介绍一些代码生成器的原理,并指导怎么编写自己的代码生成器及怎么生成一些复杂的业务，[点击学习][1]


  [1]: https://github.com/wuba/wsilk/wiki
