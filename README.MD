## To install

`$ mvn clean package`

`$ gfsh` 

`gfsh> deploy --jar=target/sum-func-1.0-SNAPSHOT.jar`

### To execute via gfsh

`gfsh> execute function --id=sum --region=<region name> --arguments=<field name to sum>`