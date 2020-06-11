![build status](https://api.travis-ci.com/ozzi-/ERC.svg?branch=master)
![open issues](https://img.shields.io/github/issues/ozzi-/erc.svg)

# External Resource Checker
ECR will check a given URL for external resources by performing static analysis. 

It supports the following tags:
- link
- script
- iframe
- object
- style
- img
- embed
- source
- track

As well as XHR requests and CSS @imports (by pattern matching) in inline tags or JS/CSS files.

## Use Cases
- Monitoring your company websites against usage of CDN's
- Scanning multiple pages for usage of derelict resources that you can own (i.E. with domain hijacking)
- Generating reports 

## Usage
```
usage: java -jar ERC.jar
 -u,--url <arg>     URL to scan for external content
 -q,--quiet         don't output further HTTP call notices
 -s,--strict        consider subdomains as external content
 -l,--secondlevel   download second level domain list
 -p,--proxy <arg>   use proxy (i.E. 127.0.0.1:8080)
 -e,--exitcode      amount of findings will be returned as exit code
 -j,--json          output results as JSON
 -d,--debug         output first 1000 characters of the response received 
```

### Strict mode
Using --strict will consider domain.com loading a resource from sub.domain.com to be external content.

### Second Level Domains
Since many top level domains exist, that consist of multiple "parts", such as co.uk, this results in problems when checking for external content. 
ERC contains a list of the most common so called second level domains, this will prevent it from interpreting foo.co.uk and bar.co.uk as the same origin (co.uk).
If you require the complete list, use the --secondlevel flag, it will download the current list (https://publicsuffix.org/list/public_suffix_list.dat).

## Example
java -jar ERC.jar -u https://github.com -s -q
```
ERC running running for - https://github.com (132521) 
******************************************************
-> Found <link> loading resource from github.githubassets.com
-> Found <link> loading resource from avatars0.githubusercontent.com
. . . 
-> Found <link> loading resource from github.githubassets.com/pinned-octocat.svg
-> Found <link> loading resource from github.githubassets.com/favicons/favicon.png
-> Found <link> loading resource from github.githubassets.com/favicons/favicon.svg
-> Found <script> loading resource from github.githubassets.com/assets/environment-bootstrap-63ce95f0.js
-> Found <script> loading resource from github.githubassets.com/assets/vendor-8df47ee1.js
-> Found <script> loading resource from github.githubassets.com/assets/frameworks-6007a3f3.js
-> Found <script> loading resource from github.githubassets.com/assets/github-bootstrap-dcb03117.js
-> Found <script> loading resource from github.githubassets.com/assets/unsupported-bootstrap-af58003f.js
-> Found <img> loading resource from github.githubassets.com/images/search-key-slash.svg
-> Found <img> loading resource from github.githubassets.com/images/modules/site/logos/airbnb-logo.png
-> Found <img> loading resource from github.githubassets.com/images/modules/site/logos/sap-logo.png
-> Found <img> loading resource from github.githubassets.com/images/modules/site/logos/ibm-logo.png
. . .
-> Found <img> loading resource from customer-stories-feed.github.com/customer_stories/yyx990803/hero.jpg
-> Found <img> loading resource from customer-stories-feed.github.com/customer_stories/jessfraz/hero.jpg

Done.
```

java -jar ERC.jar -u https://github.com -j
```
{
   "taskRunning":"https://github.com",
   "findings":[
      {
         "link":"github.githubassets.com"
      },
      {
         "link":"avatars0.githubusercontent.com"
      },
      {
         "link":"avatars1.githubusercontent.com"
      },
      {
         "link":"avatars2.githubusercontent.com"
      },
      {
         "link":"avatars3.githubusercontent.com"
      },
      {
         "link":"github-cloud.s3.amazonaws.com"
      },
      
       . . .
       
      {
         "img":"github.githubassets.com/images/modules/site/integrators/google.png"
      },
      {
         "img":"github.githubassets.com/images/modules/site/integrators/codeclimate.png"
      }
   ],
   "errors":[

   ]
}
```

## Test Page
Use the following URL:
https://gist.github.com/ozzi-/eccdc84cb352c6df628bbaef06b83e8c --> RAW
