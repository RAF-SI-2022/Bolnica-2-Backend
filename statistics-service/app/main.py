from fastapi import FastAPI, BackgroundTasks, HTTPException
from fastapi.middleware.cors import CORSMiddleware
from fastapi_utils.tasks import repeat_every
from time import time, sleep
import os
import sys
import requests
import csv
import pandas as pd
from io import StringIO
from tqdm import tqdm
from app.dto import WorldStatsResponse, CasesResponse, DeathResponse,\
        HospitalizedResponse, AvailableVaccinesResponse,\
        VaccinesResponse, TestsResponse
import math
from pymongo import MongoClient
from app.downloads import download_covid_cases, download_covid_world, download_covid_deaths,\
    download_covid_hospitalized, download_covid_available_vacciness, download_covid_tests

app = FastAPI()


origins = ["*"]

app.add_middleware(
    CORSMiddleware,
    allow_origins=origins,
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# MongoDB attributes
mongo_address = os.environ.get('MONGO_ADDRESS', 'localhost')
mongodb_uri = f'mongodb://root:example@{mongo_address}/?retryWrites=true&w=majority'
# port = 8000 
mongo_client = MongoClient(mongodb_uri)
db_covid = mongo_client['covid']

covid_data_path = 'https://covid.ourworldindata.org/data/owid-covid-data.csv'
reload_time = 60*60*6  # 6 hour


prefix = os.environ.get("prefix", "/stats")


@app.on_event("startup")
@repeat_every(seconds=reload_time)
def download_covid_data():

    download_covid_world(db_covid)

    download_covid_cases(db_covid)

    download_covid_deaths(db_covid)

    download_covid_hospitalized(db_covid)

    download_covid_available_vacciness(db_covid)

    download_covid_tests(db_covid)

    print('done download!')


@app.get(f'{prefix}/world')
async def world_stats() -> list[WorldStatsResponse]:
    
    ## TODO 
    stats = db_covid.world_stats

    stats = list(stats.find({}))

    to_return = []

    for stat in stats:
        for key in stat.keys():
            # print(type(stat[key]), key)
            if type(stat[key]) is float and math.isnan(stat[key]):
                stat[key] = "unknown"

        to_return.append(WorldStatsResponse.parse_obj(stat))
        # print(stat)

    return to_return

@app.get(f'{prefix}/covid-cases')
async def covid_cases(name : str) -> CasesResponse:
    
    stats = db_covid.cases_stats

    stats = stats.find_one({"location": name})
    # print(stats)

    ## ako ne nadje dobar stats vrati error da nema podataka za tu drzavu
    if stats==None or stats==[]:
        raise HTTPException(status_code=404, detail="Name not found")
    
    return_obj = CasesResponse.parse_obj(stats)

    return return_obj

@app.get(f'{prefix}/covid-deaths')
async def covid_deaths(name : str) -> DeathResponse:
    
    stats = db_covid.deaths_stats

    stats = stats.find_one({"location": name})
    # print(stats)

    ## ako ne nadje dobar stats vrati error da nema podataka za tu drzavu
    if stats==None or stats==[]:
        raise HTTPException(status_code=404, detail="Name not found")
    
    return_obj = DeathResponse.parse_obj(stats)

    return return_obj


@app.get(f'{prefix}/covid-hospitalized')
async def covid_hospitalized(name : str) -> list[HospitalizedResponse]:
    
    stats = db_covid.hospitalized_stats

    stats = list(stats.find({"location": name}))
    # print(stats)

    ## ako ne nadje dobar stats vrati error da nema podataka za tu drzavu
    if stats==None or stats==[]:
        raise HTTPException(status_code=404, detail="Name not found")
    
    to_return = []

    for stat in stats:
        for key in stat.keys():
            # print(type(stat[key]), key)
            if type(stat[key]) is float and math.isnan(stat[key]):
                stat[key] = "unknown"

        to_return.append(HospitalizedResponse.parse_obj(stat))
        # print(stat)

    return to_return


@app.get(f'{prefix}/available-covid-vaccines')
async def covid_hospitalized(name : str) -> AvailableVaccinesResponse:
    
    stats = db_covid.available_vaccines

    stats = stats.find_one({"location": name})
    # print(stats)
    
    ## ako ne nadje dobar stats vrati error da nema podataka za tu drzavu
    if stats==None:
        raise HTTPException(status_code=404, detail="Name not found")
    
    return_obj = AvailableVaccinesResponse.parse_obj(stats)

    return return_obj


@app.get(f'{prefix}/covid-vaccines')
async def covid_hospitalized(name : str) -> list[VaccinesResponse]:
    
    stats = db_covid.vaccination_stats

    stats = list(stats.find({"location": name}))
    # print(stats)


    ## ako ne nadje dobar stats vrati error da nema podataka za tu drzavu
    if stats==None or stats==[]:
        raise HTTPException(status_code=404, detail="Name not found")
    
    to_return = []

    for stat in stats:
        
        stat['vaccine'] = [x.strip() for x in stat['vaccine'].split(',')]

        for key in stat.keys():
            # print(type(stat[key]), key)
            if type(stat[key]) is float and math.isnan(stat[key]):
                stat[key] = "unknown"

        to_return.append(VaccinesResponse.parse_obj(stat))
        # print(stat)

    return to_return


@app.get(f'{prefix}/covid-tests')
async def covid_hospitalized(name : str) -> TestsResponse:
    stats = db_covid.world_stats

    stats = stats.find_one({"location": name})
    # print(stats)
    
    ## ako ne nadje dobar stats vrati error da nema podataka za tu drzavu
    if stats==None:
        raise HTTPException(status_code=404, detail="Name not found")
    
    iso_code = stats['iso_code']

    stats = db_covid.tests_stats

    stats = stats.find_one({"iso_code": iso_code})
    # print(stats)
    
    ## ako ne nadje dobar stats vrati error da nema podataka za tu drzavu
    if stats==None:
        raise HTTPException(status_code=404, detail="Name not found")
    
    return_obj = TestsResponse.parse_obj(stats)

    return return_obj