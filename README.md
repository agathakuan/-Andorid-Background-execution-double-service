# Andorid-Background-execution-double-service
double Service background execution,in this example implement:
useful for: backgroud execution app that has sensor-detect function.

## 2 services & 1 activity communicate with broadcast
- broadcast receiver in service
- service send to service, but action register in MainActivity
## service process main function
-main activity only use for update Graphic interface
-input service use Thread listen to accelermeter
-ouput service send System notification (even after mainActivity onPause)
