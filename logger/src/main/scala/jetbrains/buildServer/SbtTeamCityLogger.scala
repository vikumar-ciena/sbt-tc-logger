package jetbrains.buildServer

import sbt._
import Keys._

object SbtTeamCityLogger extends Plugin {

  lazy val tcLogAppender = new TCLogAppender()
  lazy val tcLogger = new TCLogger(tcLogAppender)
  lazy val tcTestListener = new TCReportListener(tcLogAppender)
  lazy val startCompilationLogger = TaskKey[Unit]("start-compilation-logger", "runs before compile")
  lazy val startTestCompilationLogger = TaskKey[Unit]("start-test-compilation-logger", "runs before compile in test")

  val tcVersion = sys.env.get("TEAMCITY_VERSION")
  val tcFound = !tcVersion.isEmpty

  override lazy val settings = if (tcFound) loggerOnSettings else loggerOffSettings

   lazy val loggerOnSettings =  Seq(
        commands += tcLoggerStatusCommand,
        testListeners += tcTestListener,
        extraLoggers := {
          val currentFunction = extraLoggers.value
          (key: ScopedKey[_]) => {
            tcLogger +: currentFunction(key)
          }
        },
        startCompilationLogger := {
             tcLogAppender.compilationBlockStart()
        },
        startTestCompilationLogger := {
             tcLogAppender.compilationTestBlockStart()
        },
        compile in Compile <<= ((compile in Compile) dependsOn startCompilationLogger)
          andFinally {tcLogAppender.compilationBlockEnd()},

        compile in Test <<= ((compile in Test) dependsOn startTestCompilationLogger)
               andFinally {tcLogAppender.compilationTestBlockEnd()}
  )

  lazy val loggerOffSettings = Seq(
        commands += tcLoggerStatusCommand
  )


  def tcLoggerStatusCommand = Command.command("sbt-teamcity-logger") {
    state => doCommand(state)
  }

  private def doCommand(state: State): State = {
    println("Plugin sbt-teamcity-logger was loaded.")
    if (tcFound) {
      println(s"TeamCity version='$tcVersion'")
    } else {
      println(s"TeamCity was not discovered. Logger was switched off.")
    }
    state
  }


}