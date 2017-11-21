# Overview
Helper library containing various classes and methods in order to support operations on liferay calendars and calendar events.

# Dependencies
Requires calendar-portlet-service.jar (currently 6.2.0.13) at compile and runtime! 
1. Either build the jar from [source](https://github.com/liferay/com-liferay-calendar) or [download](https://web.liferay.com/de/marketplace/-/mp/application/31070085) the calendar portlet and grab it from /WEB-INF/lib/.
2. Either install the jar locally or deploy it to an artifactory of your choice in order to satisfy the maven compile time dependency.
3. Copy the jar to your container's global include path (e.g. /lib/ext/ for tomcat) to make the jar available at runtime.


# Build
To install locally: 

```
mvn clean install
```

To deploy to artifactory:

```
mvn clean deploy
```



