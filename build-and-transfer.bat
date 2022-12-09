ECHO STARTING BUILD
call mvn clean install
# ECHO BUILD COMPLETED TRANSFERRING JAR FILE
# gcloud compute scp c:\git\fakenews\target\fakenews-0.1.jar wplab:/tmp
# PAUSE