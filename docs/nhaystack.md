# NHaystack

NHaystack is an open-source Niagara AX module, hosted at
[https://bitbucket.org/jasondbriggs/nhaystack](https://bitbucket.org/jasondbriggs/nhaystack),
that serves up [Project Haystack](http://project-haystack.org) data 
directly from a Niagara AX station, via a RESTful protocol.

The development of NHaystack has been funded by 
[J2 Innovations](http://www.j2inn.com).  NHaystack is licensed under the
[Academic Free License ("AFL") v. 3.0](http://opensource.org/licenses/AFL-3.0).

## Status

NHaystack is currently in beta.  This means that the project is mostly complete,
but there may be bugs.  As such, it is not ready yet for deployment in a
production environment.

## Getting Started

To get started, install nhaystack.jar into a station.  Start up workbench, 
open the nhaystack palette, and drag-and-drop the NHaystackService onto 
the /Services folder of your station.

This is all you need to do to get rolling.  Your station is now automatically
serving up all the ControlPoint objects and Histories as haystack 
['point'](http://project-haystack.org/tag/point) recs, 
via the [Haystack REST Api](http://project-haystack.org/doc/Rest). 
Many of the tags that are defined as being associated with points, like 
'kind', 'unit', 'tz', 'his', 'cur' etc are automatically generated for you.

## How ControlPoints and Histories are served up

## tagging via haystack slot

rebuild cache

## A Note on how 'id' tags are generated.

## How 'site' And 'equip' tags work.

Say something about navMeta





