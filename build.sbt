val versionAkka = "2.6.14"
val versionNacos = "2.0.0"
val versionPlay = "2.8.8"
val versionConfig = "1.4.0"
//val versionScalaCollectionCompat = "2.4.3"
val versionScalatest = "3.2.8"

ThisBuild / scalaVersion := "2.13.5"

ThisBuild / scalafmtOnCompile := true

ThisBuild / version := "2.0.0"

lazy val root = Project(id = "nacos-sdk-scala", base = file("."))
  .aggregate(nacosDocs, nacosPlayWs, nacosAkka, nacosClientScala)
  .settings(publish / skip := true)

lazy val nacosDocs = _project("nacos-docs")
  .enablePlugins(ParadoxMaterialThemePlugin, GhpagesPlugin)
  .dependsOn(nacosPlayWs, nacosAkka, nacosClientScala)
  .settings(publish / skip := true)
  .settings(
    Compile / paradoxMaterialTheme ~= {
      _.withLanguage(java.util.Locale.SIMPLIFIED_CHINESE)
        .withColor("indigo", "red")
        .withRepository(uri("https://github.com/yangbajing/nacos-sdk-scala"))
        .withSocial(
          uri("http://yangbajing.github.io/nacos-sdk-scala/"),
          uri("https://github.com/yangbajing"),
          uri("https://weibo.com/yangbajing"))
    },
    paradoxProperties ++= Map(
      "github.base_url" -> s"https://github.com/yangbajing/nacos-sdk-scala/tree/${version.value}",
      "version" -> version.value,
      "scala.version" -> scalaVersion.value,
      "scala.binary_version" -> scalaBinaryVersion.value,
      "scaladoc.akka.base_url" -> s"http://doc.akka.io/api/$versionAkka",
      "akka.version" -> versionAkka),
    git.remoteRepo := "https://github.com/yangbajing/nacos-sdk-scala.git",
    //ThisProject / GitKeys.gitReader := baseDirectory(base => new DefaultReadableGit(base)).value,
    siteSourceDirectory := target.value / "paradox" / "site" / "main",
    ghpagesNoJekyll := true)

lazy val nacosPlayWs = _project("nacos-play-ws")
  .dependsOn(nacosClientScala % "compile->compile;test->test")
  .settings(libraryDependencies ++= Seq(
    "com.typesafe.akka" %% "akka-stream-typed" % versionAkka,
    "com.typesafe.akka" %% "akka-actor-testkit-typed" % versionAkka % Test,
    ("com.typesafe.play" %% "play-ahc-ws" % versionPlay).excludeAll(ExclusionRule("com.typesafe.akka"))))

lazy val nacosAkka = _project("nacos-akka")
  .dependsOn(nacosClientScala % "compile->compile;test->test")
  .settings(
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-actor-testkit-typed" % versionAkka % Test,
      "com.typesafe.akka" %% "akka-discovery" % versionAkka))

lazy val nacosClientScala = _project("nacos-client-scala").settings(
  libraryDependencies ++= Seq(
//    "org.scala-lang.modules" %% "scala-collection-compat" % versionScalaCollectionCompat,
    "com.typesafe" % "config" % versionConfig,
    "com.alibaba.nacos" % "nacos-client" % versionNacos,
    "org.scalatest" %% "scalatest" % versionScalatest % Test))

def _project(name: String, _base: String = null) =
  Project(id = name, base = file(if (_base eq null) name else _base))
    .enablePlugins(AutomateHeaderPlugin)
    .settings(basicSettings: _*)
    .settings(publishing: _*)
//    .settings(libraryDependencies ++= Seq(
//      "io.gatling.highcharts" % "gatling-charts-highcharts" % "3.0.3" % "test,it",
//      "io.gatling"            % "gatling-test-framework"    % "3.0.3" % "test,it"))

def basicSettings =
  Seq(
    organization := "me.yangbajing.nacos4s",
    organizationName := "yangbajing",
    organizationHomepage := Some(url("https://yangbajing.me")),
    homepage := Some(url("https://yangbajing.github.cn/nacos-sdk-scala")),
    startYear := Some(2020),
    licenses += ("Apache-2.0", new URL("https://www.apache.org/licenses/LICENSE-2.0.txt")),
    headerLicense := Some(HeaderLicense.ALv2("2020", "me.yangbajing")),
    scalacOptions ++= {
      val list = Seq(
        "-encoding",
        "UTF-8", // yes, this is 2 args
        "-feature",
        "-deprecation",
        "-unchecked",
        "-Xlint",
        "-opt:l:inline",
        "-opt-inline-from",
        "-Ywarn-dead-code")
      (if (scalaVersion.value.startsWith("2.12")) "-target:jvm-1.8" else "-target:8") +: list
    },
    Compile / javacOptions ++= Seq("-source", "1.8", "-target", "1.8", "-Xlint"),
    run / javaOptions ++= Seq("-Xms128m", "-Xmx1024m", "-Djava.library.path=./target/native"),
    shellPrompt := { s =>
      Project.extract(s).currentProject.id + " > "
    },
    assembly / test := {},
    assembly / assemblyMergeStrategy := {
      case PathList("javax", "servlet", xs@_*) => MergeStrategy.first
      case PathList("io", "netty", xs@_*) => MergeStrategy.first
      case PathList("jnr", xs@_*) => MergeStrategy.first
      case PathList("com", "datastax", xs@_*) => MergeStrategy.first
      case PathList("com", "kenai", xs@_*) => MergeStrategy.first
      case PathList("org", "objectweb", xs@_*) => MergeStrategy.first
      case PathList(ps@_*) if ps.last.endsWith(".html") => MergeStrategy.first
      case "application.conf" => MergeStrategy.concat
      case "META-INF/io.netty.versions.properties" => MergeStrategy.first
      case PathList("org", "slf4j", xs@_*) => MergeStrategy.first
      case "META-INF/native/libnetty-transport-native-epoll.so" => MergeStrategy.first
      case x =>
        val oldStrategy = (assembly / assemblyMergeStrategy).value
        oldStrategy(x)
    },
    libraryDependencies ++= Seq("ch.qos.logback" % "logback-classic" % "1.2.3" % Test),
    run / fork := true,
    Test / fork := true,
    Test / parallelExecution := false)

def publishing =
  Seq(
    bintrayOrganization := Some("helloscala"),
    bintrayRepository := "maven",
    developers := List(
      Developer(
        id = "yangbajing",
        name = "Yang Jing",
        email = "yang.xunjing@qq.com",
        url = url("https://github.com/yangbajing"))),
    scmInfo := Some(
      ScmInfo(
        url("https://github.com/yangbajing/nacos-sdk-scala"),
        "scm:git:git@github.com:yangbajing/nacos-sdk-scala.git")))
