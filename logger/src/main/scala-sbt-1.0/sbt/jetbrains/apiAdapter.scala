/*
 * Copyright 2013-2017 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0.
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied.
 *
 * See the License for the specific language governing permissions
 * and limitations under the License.
 */

package sbt.jetbrains

import jetbrains.buildServer.sbtlogger.{TCLogAppender, TCLogger, TCLoggerAppender}
import sbt.{Reference, Scope, Select, Zero}

import scala.collection.mutable

object apiAdapter {

  type SessionSettings = sbt.internal.SessionSettings
  type ExtraLogger = org.apache.logging.log4j.core.Appender

  def projectScope(project: Reference): Scope = Scope(Select(project), Zero, Zero, Zero)

  def extraLogger(tcLoggers: mutable.Map[String, TCLogger],
                  tcLogAppender: TCLogAppender,
                  scope: String): ExtraLogger = {
    val appender = new TCLoggerAppender(tcLogAppender, scope)
    appender.start()
    appender
  }

}
