# Sequencium

This project has been completed by Max Collier, Megan Wood, Jackson Kerr and Shar Mathias.


### Commiting to Project

To pull work from the origin repo you can use the command `git pull origin master` (or your own branch instead of master). To commit work use `git status` `git add .` `git commit -m "my message"`. 

When pushing work, make sure that your work compiles and works as expected. Use the command `git push origin master` (or your own branch).

To switch branches use the command `git checkout my-branch` and to create new branches use the command `git checkout -b my-new-branch`.

If you are working to merge your branch to master make sure to pull latest master, checkout your branch, and merge master into it before merging the branch back to master. This will maintain a clean working version of the master branch.  

The command `rm -r */*.class` can be used to keep the origin repo clean before pushing. The rm `-f` flag will remove the files automatically.

### Building Project

To compile the project, use the command:

`javac -Xlint -cp sequencium.jar seqtournament/*.java`

To run the program, use the command:

`java seqtournament.Sequencium`

To compile the tests, use the command:

`javac -Xlint -cp sequencium.jar */*.java`

To run the test, use the command:

`java tests.MyTest`
