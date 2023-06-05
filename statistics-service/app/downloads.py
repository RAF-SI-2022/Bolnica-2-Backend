import requests
from io import StringIO
import pandas as pd
from tqdm import tqdm
import os

def download_covid_world(db_covid):
    covid_data_path = "https://raw.githubusercontent.com/owid/covid-19-data/master/public/data/latest/owid-covid-latest.csv"

    csv_data = requests.get(covid_data_path)
    text = csv_data.content.decode('utf-8')
    # print('download complete')
    data = pd.read_csv(StringIO(text))

    important_columns = ['iso_code', 'continent', 'location', 
                    'last_updated_date', 'total_cases', 
                    'new_cases', 'total_deaths', 'new_deaths', 
                    'hosp_patients', 'total_vaccinations',
                    'people_vaccinated', 'people_fully_vaccinated', 
                    'total_boosters', 'new_vaccinations']

    data = data[important_columns]

    json_data = data.to_dict('records')
    
    for i, row in tqdm(enumerate(json_data)):
        try:
            stats = db_covid.world_stats

            updates = {
                "$set": row
            }

            # print(row['location'], row['date'])
            user_id = stats.replace_one(
                {
                    "iso_code": row['iso_code']
                }, row, upsert=True)
            
            # for testing
            # if i>10:
            #     break
        except:
            import traceback
            traceback.print_exc()


def download_covid_cases(db_covid):
    download_covid_data = "https://raw.githubusercontent.com/owid/covid-19-data/master/public/data/cases_deaths/weekly_cases.csv"

    csv_data = requests.get(download_covid_data)
    text = csv_data.content.decode('utf-8')
    # print('download complete')
    data = pd.read_csv(StringIO(text))
    
    for column in tqdm(data.columns):
        if column == 'date':
            continue
        tmp_data = data[['date', column]]

        ## uzeti najnovijih 7 dana za kolonu(zemlju)
        # print('before', tmp_data.shape)
        tmp_data = tmp_data[tmp_data[column].isna()==False]
        # tmp_data = tmp_data[-7:]

        if tmp_data.shape[0]<7:
            print(f"nema data za {column}")
            continue
        # print(tmp_data)
        # print('after', tmp_data.shape)

        ## obrisati poslednjih 7 dana i upisati novih u mongo
        last_update = tmp_data.iloc[-1]['date']
        to_insert = {"location": column, "last_update": last_update}
        results = []
        for i, row in tmp_data.iterrows():
            # print(row)
            try:
                results.append({"date": row['date'], "value": row[column]})
                

            except:
                import traceback
                traceback.print_exc()
        to_insert['cases_list'] = results
        
        try:
            stats = db_covid.cases_stats

            updates = {
                "$set": row
            }

            # print(row['location'], row['date'])
            user_id = stats.replace_one(
                {
                    "location": to_insert['location']
                }, to_insert, upsert=True)
            
            # for testing
            # if i>10:
            #     break
        except:
            import traceback
            traceback.print_exc()


def download_covid_deaths(db_covid):
    download_covid_data = "https://raw.githubusercontent.com/owid/covid-19-data/master/public/data/cases_deaths/weekly_deaths.csv"

    csv_data = requests.get(download_covid_data)
    text = csv_data.content.decode('utf-8')
    # print('download complete')
    data = pd.read_csv(StringIO(text))
    
    for column in tqdm(data.columns):
        if column == 'date':
            continue
        tmp_data = data[['date', column]]

        ## uzeti najnovijih 7 dana za kolonu(zemlju)
        # print('before', tmp_data.shape)
        tmp_data = tmp_data[tmp_data[column].isna()==False]
        # tmp_data = tmp_data[-7:]

        if tmp_data.shape[0]<7:
            print(f"nema data za {column}")
            continue
        # print(tmp_data)
        # print('after', tmp_data.shape)

        ## obrisati poslednjih 7 dana i upisati novih u mongo
        last_update = tmp_data.iloc[-1]['date']
        to_insert = {"location": column, "last_update": last_update}
        results = []
        for i, row in tmp_data.iterrows():
            # print(row)
            try:
                results.append({"date": row['date'], "value": row[column]})
                

            except:
                import traceback
                traceback.print_exc()
        to_insert['deaths_list'] = results
        
        try:
            stats = db_covid.deaths_stats

            updates = {
                "$set": row
            }

            # print(row['location'], row['date'])
            user_id = stats.replace_one(
                {
                    "location": to_insert['location']
                }, to_insert, upsert=True)
            
            # for testing
            # if i>10:
            #     break
        except:
            import traceback
            traceback.print_exc()


def download_covid_hospitalized(db_covid):
    download_covid_data = "https://raw.githubusercontent.com/owid/covid-19-data/master/public/data/hospitalizations/covid-hospitalizations.csv"

    csv_data = requests.get(download_covid_data)
    text = csv_data.content.decode('utf-8')
    # print('download complete')
    data = pd.read_csv(StringIO(text))

    # print(data.shape)
    data = data[data['indicator'] == 'Daily ICU occupancy']
    # print(data.shape)

    
    data = data.rename(columns={"entity": "location"})

    json_data = data.to_dict('records')

    stats_metadata = db_covid.hospitalized_metadata
    metadata = list(stats_metadata.find({"first_load": True}))
    if metadata in [None, []]:
        stats_metadata.insert_one({"first_load": True})
        ## koristi 
        print("prvi load")
        stats = db_covid.hospitalized_stats
        stats.insert_many(json_data)
        return

    for record in tqdm(json_data):
            
        try:
            stats = db_covid.hospitalized_stats

            updates = {
                "$set": record
            }

            # print(row['location'], row['date'])
            user_id = stats.replace_one(
                {
                    "location": record['location'],
                    "date": record['date']
                }, record, upsert=True)
            
            # for testing
            # if i>10:
            #     break
        except:
            import traceback
            traceback.print_exc()

def download_covid_available_vacciness(db_covid):
    download_covid_data = "https://raw.githubusercontent.com/owid/covid-19-data/master/public/data/vaccinations/locations.csv"

    csv_data = requests.get(download_covid_data)
    text = csv_data.content.decode('utf-8')
    # print('download complete')
    data = pd.read_csv(StringIO(text))

    ## podeli vaccines po zarezu
    data['vaccines'] = data['vaccines'].str.split(',')
    
    data['vaccines'] = data.apply(lambda row: [val.strip() for val in row['vaccines']], axis=1)

    
    json_data = data.to_dict('records')

    for record in tqdm(json_data):
            
        try:
            stats = db_covid.available_vaccines

            updates = {
                "$set": record
            }

            # print(row['location'], row['date'])
            user_id = stats.replace_one(
                {
                    "location": record['location']
                }, record, upsert=True)
            

            ## TODO za svaki location download vaccination count
            download_covid_vaccinations(db_covid, record['location'], record['iso_code'])

            # for testing
            # if i>10:
            #     break
        except:
            import traceback
            traceback.print_exc()

def download_covid_vaccinations(db_covid, location, iso_code):

    download_covid_data = f'https://raw.githubusercontent.com/owid/covid-19-data/master/public/data/vaccinations/country_data/{location}.csv'

    csv_data = requests.get(download_covid_data)
    text = csv_data.content.decode('utf-8')
    # print('download complete')
    data = pd.read_csv(StringIO(text))


    data = data.fillna(0)

    data['iso_code'] = data.apply(lambda row: iso_code, axis=1)

    json_data = data.to_dict('records')


    stats_metadata = db_covid.vaccination_metadata
    metadata = list(stats_metadata.find({"first_load": location}))
    if metadata in [None, []]:
        stats_metadata.insert_one({"first_load": location})
        ## koristi 
        # print("prvi load")
        stats = db_covid.vaccination_stats
        stats.insert_many(json_data)
        return

    for row in json_data:

        # row['iso_code'] = iso_code
        try:
            stats = db_covid.vaccination_stats

            updates = {
                "$set": row
            }


            # print(row['location'], row['date'])
            user_id = stats.replace_one(
                {
                    "location": row['location'],
                    "date": row['date']
                }, row, upsert=True)
            
            # for testing
            # if i>10:
            #     break
        except:
            import traceback
            traceback.print_exc()


def download_covid_tests(db_covid):

    download_covid_data = f'https://raw.githubusercontent.com/owid/covid-19-data/master/public/data/testing/covid-testing-latest-data-source-details.csv'

    csv_data = requests.get(download_covid_data)
    text = csv_data.content.decode('utf-8')
    # print('download complete')
    data = pd.read_csv(StringIO(text))


    data = data.fillna(0)


    data = data.rename(columns={"Entity": "entity", "Date": "date", "ISO code":"iso_code", 
                                "Number of observations":"number_of_observations",
                                "Cumulative total": "cumulative_total"})

    json_data = data.to_dict('records')

    for row in tqdm(json_data):

        # row['iso_code'] = iso_code
        try:
            stats = db_covid.tests_stats

            updates = {
                "$set": row
            }

            # print(row['location'], row['date'])
            user_id = stats.replace_one(
                {
                    "entity": row['entity'],
                    "date": row['date']
                }, row, upsert=True)
            
            # for testing
            # if i>10:
            #     break
        except:
            import traceback
            traceback.print_exc()


