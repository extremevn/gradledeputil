# Gradle dependency version plugin
  
A Gradle plugin that is built on top of [Gradle Versions Plugin.](https://github.com/ben-manes/gradle-versions-plugin) for:
- Checking all dependency which is outdated
- Make a report to target client (such as: upload outdated dependencies information file with message to ChatWork client site) 
  
## Getting Started

### Installation

You can add this plugin to your top-level build script using the following configuration:

#### `plugins` block:


```groovy
plugins {
  id "vn.com.extremevn.gradle.deputil" version "<plugin latest version>"
}
```
or via the

#### `buildscript` block:
```groovy
apply plugin: "vn.com.extremevn.gradle.deputil"

buildscript {
  repositories {
    maven()
    mavenCentral()
  }

  dependencies {
    classpath "vn.com.extremevn.gradle:gradle-dependency-util:<plugin latest version>"
  }
}
```
###  Configuration

You can add configuration values by using `dependencyVersionExt` extension or system environment variables
#### Using`dependencyVersionExt` extension:
```groovy
dependencyUtil {
    chatworkRoomId = <roomID>
    chatworkToken = "<chatwork's api token>"
    chatworkDefaultMessage = "<a notification message>"
}
```
#### Using system environment variables extension:
Depend on your running os, defined following environment variables. For example:
- For Linux: add following script to /etc_profile or ~/.bash_profile or ~/.bash_rc
```shell
export CHATWORK_TOKEN="<your chatwork api sender account token>"
export CHATWORK_ROOMID="<your chatwork roomId which will received notification message>"
export CHATWORK_DEFAULT_MESSAGE="<default notification message content>"
```
- For MacOS: add following script to ~/zshrc
```shell
export CHATWORK_TOKEN="<your chatwork api sender account token>"
export CHATWORK_ROOMID="<your chatwork roomId which will received notification message>"
export CHATWORK_DEFAULT_MESSAGE="<default notification message content>"
```
- For Windows 10 and Windows 8:
  1. In Search, search for and then select: System (Control Panel)
  2. Click the Advanced system settings link.
  3. Click Environment Variables. In the section System Variables click New if not yet set up before.
  4. In the New System Variable window, specify the name and value of environment variables. Click OK. Close all remaining windows by clicking OK.
  
###  Usage

#### `reportToChatwork` task

This task will do:
- Check for all dependencies if there is version updates
- Process dependencies and filtered dependencies whose current version is outdated
- Upload file which contains outdated dependencies and message to ChatWork client site

#### Multi-project build

In a multi-project build, running this task in the root project will generate a consolidated/merged
report for dependency updates in all subprojects. Alternatively, you can run the task separately in
each subproject to generate separate reports for each subproject.


## Contributor

- [Justin Lewis](https://github.com/justin-lewis) (Maintainer)
  
## License
[MIT](LICENSE)


