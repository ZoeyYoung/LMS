// import com.github.play2war.plugin._

name := """LMS"""

version := "1.1"

// Play2WarPlugin.play2WarSettings

// Play2WarKeys.servletVersion := "3.1"

// Play2WarKeys.disableWarningWhenWebxmlFileFound := true

playJavaSettings

ebeanEnabled := false

libraryDependencies ++= Seq(
    javaCore,
    javaJpa,
    "javax.inject" % "javax.inject" % "1",
    "org.springframework" % "spring-context" % "3.2.3.RELEASE",
    "org.springframework" % "spring-expression" % "3.2.3.RELEASE",
    "org.springframework.data" % "spring-data-jpa" % "1.4.2.RELEASE",
    "org.hibernate" % "hibernate-entitymanager" % "3.6.10.Final",
    "org.mockito" % "mockito-core" % "1.9.5" % "test",
    "mysql" % "mysql-connector-java" % "5.1.31",
    "smartrics.restfixture" % "smartrics-RestClient" % "2.1",
    "apache-httpclient" % "commons-httpclient" % "3.1",
    "org.json" % "json" % "20140107"
)
