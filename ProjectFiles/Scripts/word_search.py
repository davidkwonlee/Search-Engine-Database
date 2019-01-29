
if __name__ == "__main__":
    
    writer_file = open('inverted_test.txt', 'r')
    retrieved_urls = {}
    
    for values in writer_file :
        k = values.strip().split(" - ")
        v = k[1].strip().split(" ; ")
        for e in v:
            v2 = e.split(",")
            if retrieved_urls.get(k[0].strip()) == None:
                retrieved_urls[k[0].strip()] = [tuple(v2)]
            else:
                retrieved_urls[k[0].strip()].append(tuple(v2))
#-------------------------------------------------------------------
#Handle urls that dont have idf(i.e. tuple out of bound exceptions)
#if no idf, then remove it
        try:
            retrieved_urls[k[0].strip()] = sorted(retrieved_urls.get(k[0].strip()), key=lambda x: x[1], reverse=True)
        except:
            del retrieved_urls[k[0]]
            continue
#-------------------------------------------------------------------
    writer_file.close()
    
    for word in retrieved_urls:
        if len(retrieved_urls.get(word)) > 10:
            retrieved_urls[word] = retrieved_urls[word][0:10]
            
#-------------------------------------------------------------------
#Handle query stuff
            
#populates url_query_idf table to use in MYSQL database
#-------------------------------------------------------------------
    writer = open('url_query_idf_populator', 'w')
    counter_var = 0
    i = 0
    for keys in retrieved_urls:
        url_list = []
        for counter in retrieved_urls[keys]:
            url_list.append((counter[0],keys, counter[1]))
            counter_var = counter_var + 1
            insert_statement = "INSERT IGNORE INTO url_query_idf(ID,url,query_key,idf) VALUES('{}','{}','{}','{}') ON DUPLICATE KEY UPDATE query_key = query_key;\n".format(counter_var, counter[0], keys, counter[1])
            
            try:
                writer.write(insert_statement)
            except Exception as e:
                print(e)
                continue
    
        if i == 20:
            print(i)
            
        if i%500 == 0:
            print(i)
            
        i = i+1;
        
    writer.close()
   
#-------------------------------------------------------------------
#populates results table to use in MYSQL database

    writer = open('populator', 'w')
    i = 0
    for keys in retrieved_urls:
        url_list = []
        for counter in retrieved_urls[keys]:
            url_list.append(counter[0])
        try:
            writer.write("INSERT INTO results(query_key, url_1, url_2, url_3, url_4, url_5, url_6, url_7, url_8, url_9, url_10) VALUES('{}','{}','{}','{}','{}','{}','{}','{}','{}','{}','{}') ON DUPLICATE KEY UPDATE query_key = query_key;\n".format(keys, url_list[0],url_list[1],url_list[2],url_list[3],url_list[4],url_list[5],url_list[6],url_list[7],url_list[8],url_list[9]))
        except IndexError:
            modify_insert = "INSERT INTO results(query_key, url_1, url_2, url_3, url_4, url_5, url_6, url_7, url_8, url_9, url_10) VALUES('{}',".format(keys)
            for range_of_list in range(1,len(url_list)):
                if range_of_list != len(url_list):
                    modify_insert+="'{}'".format(str(url_list[range_of_list-1]))
                    modify_insert+=","
                    
            modify_insert+="'{}'".format(str(url_list[len(url_list)-1]))
            
            for remaining_range_of_list in range(0, 10-len(url_list)): 
                modify_insert+=",'N/A'"
                
            modify_insert+=") ON DUPLICATE KEY UPDATE query_key = query_key;\n"

            writer.write(modify_insert)
            continue

        except Exception as e: 
            continue
        
        print(i)
            
        i = i+1;

    writer.close()


    
    
    
    
    
        
    
    
    
    
        
    
    
    
    
        
    
    
    
    
        
    
    
    
    
        
    
    
    
    
    
    
    
    
        
    
        
