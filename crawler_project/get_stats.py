import pandas as pd
import http

with open("../crawler_project/src/results/fetch_wsj.csv", "r") as file1:
    fetch_df = pd.read_csv(file1, header=0)
    status_code = fetch_df.groupby(fetch_df["Status"]).count().to_dict()["URL"]
    fetch_attempts = len(fetch_df)
    fetch_success = len(fetch_df[fetch_df["Status"] <= 299])
    fetch_fail = len(fetch_df[fetch_df["Status"] >= 300])

with open("../crawler_project/src/results/visit_wsj.csv", "r") as file2:
    visit_df = pd.read_csv(file2, header=0)
    content_types = visit_df.groupby(visit_df["Content-Type"]).count().to_dict()["URL"]
    total_urls_extracted = visit_df["# of Outlinks"].sum()
    lt_1KB = len(visit_df[visit_df["Size(Bytes)"] < 1024])
    lt_10KB = len(visit_df[(1024 <= visit_df["Size(Bytes)"]) & (visit_df["Size(Bytes)"] < 10 * 1024)])
    lt_100KB = len(visit_df[(10 * 1024 <= visit_df["Size(Bytes)"]) & (visit_df["Size(Bytes)"] < 100 * 1024)])
    lt_1mb = len(visit_df[(100 * 1024 <= visit_df["Size(Bytes)"]) & (visit_df["Size(Bytes)"] < 1024 * 1024)])
    gt_1mb = len(visit_df[1024 * 1024 <= visit_df["Size(Bytes)"]])

with open("../crawler_project/src/results/urls_wsj.csv", "r") as file3:
    urls_df = pd.read_csv(file3)
    temp_df = urls_df.drop_duplicates(subset='URL', keep="first")
    extracted = len(temp_df)
    within = len(temp_df[temp_df["URL Type"] == "OK"])
    outside = len(temp_df[temp_df["URL Type"] == "N_OK"])

with open("../crawler_project/src/results/crawlReport_wsj.txt", "w") as file:
    lines = ["Name: Rashmi Bhaskar\n","USC ID: 4408460333\n","News site crawled: wsj.com\n","Number of threads: 18\n","\n"]
    lines.extend([f"Fetch Statistics:\n","=================\n",f"# fetches attempted: {fetch_attempts}\n",f"# fetches succeeded: {fetch_success}\n",f"# fetches failed or aborted: {fetch_fail} \n","\n"])
    lines.extend([f"Outgoing URLs:\n","=================\n",f"Total URLs extracted: {total_urls_extracted} \n",f"# unique URLs extracted: {extracted} \n",f"# unique URLs within News Site: {within}  \n",f"# unique URLs outside News Site: {outside} \n","\n"])
    lines.extend(["Status Codes:\n","=================\n"])

    for code in sorted(status_code):
        lines.extend([f"{code} {http.HTTPStatus(code).phrase}: {status_code[code]}\n"])
    lines.extend(["\n","File Sizes:\n","=================\n",f"< 1KB: {lt_1KB}\n",f"1KB ~ <10KB: {lt_10KB}\n",f"10KB ~ <100KB: {lt_100KB}\n",f"100KB ~ <1MB: {lt_1mb}\n",f">= 1MB: {gt_1mb}\n","\n",f"Content Types:\n","=================\n"])
    for content in sorted(content_types.keys()):
        lines.extend([f"{content}: {content_types[content]}\n"])
    
    file.writelines(lines)