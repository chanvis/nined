# nineD Admin Services

This is the codebase for the NineD services.

Below are the services available as part of this codebase.

|SERIVCE NAME | PORT | COMMENTS |
--- | --- | ---
nined-nginx | 9000 (mapped to port 80 of the nginx container) 9001 (mapped to port 443 of the nginx container) | Using official Docker image nginx:1.17
nined-service-discovery | 8761 | internal port |
nined-api-gateway | 8765 | internal port|
nined-user-service | 8762 |internal port |

## Prerequisite
1. Docker latest version (docker engine required 1.13.0+).
2. Nined DB. Ref [here](https://github.com/NineDDigital/nined-database).
3. In docker-compose file, update the environment specific property values 

## Run
In the root directory, run below command to build all docker images and start the containers

```bash
docker-compose up --build
```
To start containers in detach mode
```bash
docker-compose up -d --build
```


## Sample JWT tokens to be created for users
```javascript 

eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJwdmFkbWluIiwiYXV0aG9yaXRpZXMiOlsiMTQiLCIxOSIsIjI0IiwiMzQiXSwidXNlcklkIjoxLCJjbGllbnRJZCI6MSwib3JnSWQiOjEsInJvbGUiOiJST0xFX1BST1ZJREVSX0FETUlOIiwibGFuZyI6ImVuX1VTIiwiaWF0IjoxNTgzNzg2NTI2LCJleHAiOjE1ODM3OTAxMjYsImp0aSI6ImM3NzEzNWFiLWU2ODgtNDlhMS04ZjZhLWZiZTAzNTYxOWRlZiJ9.rwNuQqIS8degysCk6y4txoOMHciWm3ObfPU42M1YYQrMrBoxcB_OUtNq49fcaLTiMZL0C_j_L6bj08DVl3HKDA

eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJwdmFkbWluIiwiYXV0aG9yaXRpZXMiOlsiLTEiXSwidXNlcklkIjoxLCJjbGllbnRJZCI6MSwib3JnSWQiOjEsInJvbGUiOiJST0xFX1BST1ZJREVSX0FETUlOIiwibGFuZyI6ImVuX1VTIiwiaWF0IjoxNTg0MzMzNjIzLCJleHAiOjE1ODQzMzQ1MjMsImp0aSI6IjgyYzllYmNkLTFkYjktNDg0MC04YjFmLTdlYzBmZmEyMjY5ZiJ9.THFCHdpueByISTxGMxlGPe1xy0_nD4_VxANFoqKSwv5_1D8ak-9SmcvhJBHOkXYN0blWSnVmuw_e7ZTIeqFqkg

```

## Application Links
Address | Description
--- | ---
http://<\<docker-host>\>:9000/swagger-ui.html | API Doc.
