from pydantic import BaseModel
import datetime
from typing import Union

class WorldStatsResponse(BaseModel):
    iso_code : str
    continent: str
    location: str
    last_updated_date: datetime.date
    total_cases: Union[int,str]
    new_cases: Union[int,str]
    total_deaths: Union[int,str]
    new_deaths: Union[int,str]
    hosp_patients: Union[int,str]
    total_vaccinations: Union[int,str]
    people_vaccinated: Union[int,str]
    people_fully_vaccinated: Union[int,str]
    total_boosters: Union[int,str]
    new_vaccinations: Union[int,str]


class DateAndValue(BaseModel):
    date: datetime.date
    value: int

class CasesResponse(BaseModel):
    location: str
    cases_list: list[DateAndValue]
    last_update: datetime.date


class DeathResponse(BaseModel):
    location: str
    deaths_list: list[DateAndValue]
    last_update: datetime.date


class HospitalizedResponse(BaseModel):
    location: str
    date: datetime.date
    value: int
    

class AvailableVaccinesResponse(BaseModel):

    location: str
    last_observation_date: datetime.date
    vaccines: list[str]



class VaccinesResponse(BaseModel):
    location: str
    date : datetime.date
    vaccine: list[str]
    total_vaccinations: int
    people_vaccinated: int
    people_fully_vaccinated: int
    total_boosters: int

class TestsResponse(BaseModel):
    iso_code: str
    entity: str
    date : datetime.date
    number_of_observations: int
    cumulative_total: int
