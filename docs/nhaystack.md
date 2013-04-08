# ![NHaystack](tag.png) NHaystack

NHaystack is an open-source [Niagara AX](http://www.niagaraax.com/) module
that serves up [Project Haystack](http://project-haystack.org) data 
directly from a Niagara AX station, via a 
[RESTful](https://en.wikipedia.org/wiki/Representational_state_transfer) protocol.

NHaystack is licensed under the
[Academic Free License ("AFL") v. 3.0](http://opensource.org/licenses/AFL-3.0).
NHaystack is hosted at
[https://bitbucket.org/jasondbriggs/nhaystack](https://bitbucket.org/jasondbriggs/nhaystack).
The development of NHaystack has been funded by 
[J2 Innovations](http://www.j2inn.com).  

## Status

NHaystack is currently in beta.  This means that the project is mostly complete,
but there may be bugs.  As such, it is not ready yet for deployment in a
production environment.

## Getting Started

To get started, install nhaystack.jar into an AX station.  Then 
open the nhaystack palette in Workbench, and drag-and-drop the NHaystackService 
onto the /Services folder of your station.

This is all you need to do to get rolling.  Your station is now automatically
serving up all the ControlPoint objects and Histories as haystack 
[point][point] recs, via the 
[Haystack REST Api](http://project-haystack.org/doc/Rest). 
Many of the tags that are defined as being associated with points, like 
[kind][kind], [unit][unit], [tz][tz], [his][his], [cur][cur], 
etc. are automatically generated for you.

## How point recs are generated.

In Haystack, a [point][point] [rec][rec] can have a [cur][cur] tag,
which indicates the point has capability for subscription to its real-time 
current value, and it can have a [his][his] tag, which indicates that a 
point is historized with a history log of timestamp/value pairs.

This is rather different than how ControlPoints and Histories work in AX. In 
AX, there is one object in the station database which represents the point,
(including its current value, actions to command it, and so forth), and a 
different object to represent its historical log of timestamp/value pairs. 
NHaystack automatically maps these two objects together so that only one 
Haystack [rec][rec] is generated, with both a [cur][cur] tag and a 
[his][his] tag.  If only one of the objects is present, then either 
the [cur][cur] tag or this [his][his] tag is generated, but not both.  Lets 
look at a couple of examples showing how this works.  

### A simple station

First lets take a simple example of a Niagara station that has two SineWaves 
in it (those are found in the kitControl module), with no history collection 
for those SineWaves.  There are two ControlPoints in this station, and 
two Histories (the default Audit and Log histories):

    station:|slot:/Foo/SineWave1
    station:|slot:/Foo/SineWave2
    history:/nhaystack_simple/AuditHistory
    history:/nhaystack_simple/LogHistory

This station will automatically have four [point][point] recs: two for the 
SineWaves, and two for the Histories.  The first two will have a [cur][cur] 
tag, but no [his][his] tag, and the second two will have a [his][his] tag but 
no [cur][cur] tag.

    {point, cur} station:|slot:/Foo/SineWave1           
    {point, cur} station:|slot:/Foo/SineWave2           
    {point, his} history:/nhaystack_simple/AuditHistory 
    {point, his} history:/nhaystack_simple/LogHistory   

Now lets add a NumericCov extension to SineWave1 and enable it.
Whenever a structural change is made to a station, by adding or removing a
component or history, there is an action on the NHaystackService called
`rebuildCache` that we must invoke.  Invoking `rebuildCache` causes the 
NHaystack Server to take a pass through the station and rebuild all of its 
internal data structures, so it can keep track of how everything in the 
station is interrelated.

After we invoke `rebuildCache`, the station will look like this:

    {point, cur, his} station:|slot:/Foo/SineWave1           
    {point, cur}      station:|slot:/Foo/SineWave2           
    {point, his}      history:/nhaystack_simple/AuditHistory 
    {point, his}      history:/nhaystack_simple/LogHistory   
    ----              history:/nhaystack_simple/SineWave1    

The station still has the same four point recs.  However, a [his][his] tag has
been added to the SineWave1 point.  If a hisRead() on that point is performed,
the NHaystack Server will fetch the timestamp/value pairs for the 
associated history.  The history for SineWave1 does not have its own rec
created because it is not necessary.

By the way, the [id][id] tags that are generated for these recs consist of:
a single character, either "c", or "h" (which indicates whether the rec comes
from the ComponentSpace or HistorySpace), followed by a dot, and then followed
by a Uri-friendly Base64 encoding of either the slotPath or the historyId.

As a final example from this simple station, lets delete SineWave1 and then 
invoke `rebuildCache`.  The station now looks like this:

    {point, cur} station:|slot:/Foo/SineWave2           
    {point, his} history:/nhaystack_simple/AuditHistory 
    {point, his} history:/nhaystack_simple/LogHistory   
    {point, his} history:/nhaystack_simple/SineWave1    

We still have four point recs.  However, the first rec is now gone.  In its
place we have a point rec for this SineWave1 history.  Note that this rec
has a different id than the rec that has disappeared.

### A more complex station

Now lets look at a more complex station -- one that is similar to what
one often sees on an AX Supervisor.

A very frequent case for supervisors is that they just import lots of histories
from jaces.

    ----         station:|slot:/Drivers/NiagaraNetwork/nhaystack_jace1/histories/Remote_SineWave1 
    ----         station:|slot:/Drivers/NiagaraNetwork/nhaystack_jace1/histories/Remote_SineWave2 
    ----         station:|slot:/Drivers/NiagaraNetwork/nhaystack_jace2/histories/Remote_SineWave1 
    ----         station:|slot:/Drivers/NiagaraNetwork/nhaystack_jace2/histories/Remote_SineWave2 
    {point, his} history:/nhaystack_sup/AuditHistory                                              
    {point, his} history:/nhaystack_sup/LogHistory                                                
    {point, his} history:/nhaystack_jace1/SineWave1                                               
    {point, his} history:/nhaystack_jace1/SineWave2                                               
    {point, his} history:/nhaystack_jace2/SineWave1                                               
    {point, his} history:/nhaystack_jace2/SineWave2                                               

This station has six recs:  two for the station's own audit and log histories, 
and four for the imported histories.  Note that the four NiagaraHistoryImport 
objects do not have a point associated with them.

Now lets change the station by importing the ControlPoints for the first jace,
and running `rebuildCache`.  Here is what the station looks like now:

    {point, cur, his} station:|slot:/Drivers/NiagaraNetwork/nhaystack_jace1/points/SineWave1           
    {point, cur, his} station:|slot:/Drivers/NiagaraNetwork/nhaystack_jace1/points/SineWave2           
    ----              station:|slot:/Drivers/NiagaraNetwork/nhaystack_jace1/histories/Remote_SineWave1 
    ----              station:|slot:/Drivers/NiagaraNetwork/nhaystack_jace1/histories/Remote_SineWave2 
    ----              station:|slot:/Drivers/NiagaraNetwork/nhaystack_jace2/histories/Remote_SineWave1 
    ----              station:|slot:/Drivers/NiagaraNetwork/nhaystack_jace2/histories/Remote_SineWave2 
    {point, his}      history:/nhaystack_sup/AuditHistory                                              
    {point, his}      history:/nhaystack_sup/LogHistory                                                
    ----              history:/nhaystack_jace1/SineWave1                                               
    ----              history:/nhaystack_jace1/SineWave2                                               
    {point, his}      history:/nhaystack_jace2/SineWave1                                               
    {point, his}      history:/nhaystack_jace2/SineWave2                                               

Note that the two histories from jace1 no longer have their own rec.  Instead,
the two imported points know about those histories, and will 
export them via hisRead().

## Tagging via the "haystack" slot

For many use cases of NHaystack, you will not need to do any explicit tagging
on the station.  However, sometimes you want to actually add tags to the recs
that are generated.  NHaystack supports this via the following convention: if
there is a slot called "haystack" on a Component, and that slot is of type 
"nhaystack:HDict", then the tags which are saved in that slot are exported
in the rec along with all the auto-generated tags.  There is a Workbench
FieldEditor  for HDict which allows you to edit these tags.  In addition, this
FieldEditor shows you all the auto-generated tags (though you cannot edit 
them). 

Note that tagging of Histories is not currently supported.  A future version
of NHaystack will support this.

## How site and equip tags work.

In addition to tagging point recs, you may also want to include [site][site] 
and [equip][equip] recs in your station, and link everything together via 
[siteRef][siteRef] and [equipRef][equipRef] pointers.

NHaystack does support creating this site->equip->point hierarchy.  Lets go
back to our simple example station, except we'll add a few more points.  Note
that this time we have also included the slot path for the parent "Foo" folder.

    ----         station:|slot:/Foo
    {point, cur} station:|slot:/Foo/SineWave1           
    {point, cur} station:|slot:/Foo/SineWave2           
    {point, cur} station:|slot:/Foo/SineWave3           
    {point, cur} station:|slot:/Foo/SineWave4           
    {point, his} history:/nhaystack_simple/AuditHistory 
    {point, his} history:/nhaystack_simple/LogHistory   

If you look in the nhaystack palette, you'll see that there are Components for
Site and Equip. Drag a Site over and drop it anywhere in the ComponentSpace, 
and then run `rebuildCache`.  The station will now look like this:

    {site}       station:|slot:/MySite
    ----         station:|slot:/Foo
    {point, cur} station:|slot:/Foo/SineWave1           
    {point, cur} station:|slot:/Foo/SineWave2           
    {point, cur} station:|slot:/Foo/SineWave3           
    {point, cur} station:|slot:/Foo/SineWave4           
    {point, his} history:/nhaystack_simple/AuditHistory 
    {point, his} history:/nhaystack_simple/LogHistory   

Site and Equip already have a "haystack" slot, so you don't have to add
one via the SlotSheet.  Now, if you bring up the HDict FieldEditor for the
Site, you'll see that there are quite a few tags you can fill out, like the
various "geo" tags.

The next thing we want to do is create an Equip and have it reference the Site.
However, instead of dragging an Equip object, there is a more flexible 
way to do this, which we call "implicit equip tagging".  Go to the "Foo" 
folder, add a "haystack" slot, and use the FieldEditor to give Foo a single 
tag called "equip".  Also, go ahead and add a "siteRef" tag, and point that 
to "MySite".  (Then do `rebuildCache` as usual).  The station now looks 
something like this.

    {site}                     station:|slot:/MySite
    {equip,siteRef="MySite"}   station:|slot:/Foo
    {point,cur,equipRef="Foo"} station:|slot:/Foo/SineWave1
    {point,cur,equipRef="Foo"} station:|slot:/Foo/SineWave2
    {point,cur,equipRef="Foo"} station:|slot:/Foo/SineWave3
    {point,cur,equipRef="Foo"} station:|slot:/Foo/SineWave4
    {point,his}                history:/nhaystack_simple/AuditHistory
    {point,his}                history:/nhaystack_simple/LogHistory

A couple of things have happened here.  First, the NHaystack Server noticed
that you put a haystack slot on "Foo", so it generated a rec for you.  The
second thing that has happened is that all of the SineWaves, by virtue of the 
fact that they can be found under "Foo", automatically know that "Foo" is their
parent equip.  As such they have equipRefs auto-generated.  The SineWaves
do not have to be direct children of "Foo" -- they can be grand-children,
great-grand-children, etc.

This is a powerful feature of nhaystack, because it allows you to copy points
without having to go back and re-tag their equipRef.  For instance, if you
make a duplicate of "Foo" called "Bar", and then run `rebuildCache`, the
station will look like this:

    {site}                     station:|slot:/MySite
    {equip,siteRef="MySite"}   station:|slot:/Foo
    {point,cur,equipRef="Foo"} station:|slot:/Foo/SineWave1
    {point,cur,equipRef="Foo"} station:|slot:/Foo/SineWave2
    {point,cur,equipRef="Foo"} station:|slot:/Foo/SineWave3
    {point,cur,equipRef="Foo"} station:|slot:/Foo/SineWave4
    {equip,siteRef="MySite"}   station:|slot:/Bar
    {point,cur,equipRef="Bar"} station:|slot:/Bar/SineWave1
    {point,cur,equipRef="Bar"} station:|slot:/Bar/SineWave2
    {point,cur,equipRef="Bar"} station:|slot:/Bar/SineWave3
    {point,cur,equipRef="Bar"} station:|slot:/Bar/SineWave4
    {point,his}                history:/nhaystack_simple/AuditHistory
    {point,his}                history:/nhaystack_simple/LogHistory

All of the SineWaves anywhere under the "Foo" folder point to "Foo" for 
their equipRefs, and all of the SineWaves anywhere under the "Bar" folder 
point to "Bar".

Sometimes you want to be able to explicitly tag certain points
as belonging a given equip.  To do this, you can use the Equip object found in 
the palette.  Lets drag an equip over and call it "TrashCompactor".  Edit the
equip so it points to "MySite".  Then lets take both "SineWave4" objects and 
edit their tags via the FieldEditor so that they explicitly reference the 
TrashCompactor.  After re-running `rebuildCache`, our station will look 
like this:

    {site}                                station:|slot:/MySite
    {equip,siteRef="MySite"}              station:|slot:/TrashCompactor
    {equip,siteRef="MySite"}              station:|slot:/Foo
    {point,cur,equipRef="Foo"}            station:|slot:/Foo/SineWave1
    {point,cur,equipRef="Foo"}            station:|slot:/Foo/SineWave2
    {point,cur,equipRef="Foo"}            station:|slot:/Foo/SineWave3
    {point,cur,equipRef="TrashCompactor"} station:|slot:/Foo/SineWave4
    {equip,siteRef="MySite"}              station:|slot:/Bar
    {point,cur,equipRef="Bar"}            station:|slot:/Bar/SineWave1
    {point,cur,equipRef="Bar"}            station:|slot:/Bar/SineWave2
    {point,cur,equipRef="Bar"}            station:|slot:/Bar/SineWave3
    {point,cur,equipRef="TrashCompactor"} station:|slot:/Bar/SineWave4
    {point,his}                           history:/nhaystack_simple/AuditHistory
    {point,his}                           history:/nhaystack_simple/LogHistory

<!-- links ----------------------->

[cur]:   http://project-haystack.org/tag/cur
[equip]: http://project-haystack.org/tag/equip
[his]:   http://project-haystack.org/tag/his
[id]:    http://project-haystack.org/tag/id
[kind]:  http://project-haystack.org/tag/kind
[point]: http://project-haystack.org/tag/point
[site]:  http://project-haystack.org/tag/site
[tz]:    http://project-haystack.org/tag/tz
[unit]:  http://project-haystack.org/tag/unit

[rec]: http://project-haystack.org/doc/TagModel#entities



