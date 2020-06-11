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

