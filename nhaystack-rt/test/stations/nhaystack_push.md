# Stations for testing nHaystack history push

`nhaystack_sender` and `nhaystack_receiver` are stations designed for testing
the history push feature of nHaystack.  `nhaystack_receiver` acts as a Project
Haystack server, and receives history data using `hisWrite` calls made by the
sending station: `nhaystack_sender`.

While nHaystack is being used as the test Project Haystack implementation
here, the test should also be applicable to other implementations of Project
Haystack including SkySpark and WideSky.

## Deployment

The stations can be instantiated from the templates using the usual manner.
The sender assumes the receiver's `admin` password has been set to
`nHaystack2022`.

Deploy the receiver, then, when running, connect to it on port 54911 and
check each of the `NumericCov` entities are enabled:

 * `Config`→`Equip1`→`SineWave1`→`NumericCov`
 * `Config`→`Equip1`→`SineWave2`→`NumericCov`
 * `Config`→`SineWave4`→`NumericCov`
 * `Config`→`SineWave5`→`NumericCov`

This will enable them to permit `hisWrite` operations: the history config
settings are configured to essentially be "inactive" all the time so that
the `NumericCov` does not itself write history samples into the trend storage.

With the receiver running, deploy the sender.  Go to
`Config`→`Drivers`→`NHaystackNetwork`→`UpstreamNHaystack` and check that a
password is set for the upstream station, then right-click on
`UpstreamNHaystack` and select `Actions`→`Ping`.

Then, check the sender's `NumericCov` entities are enabled:

 * `Config`→`Equip1`→`SineWave1`→`NumericCov`
 * `Config`→`SineWave4`→`NumericCov`
 * `Config`→`SineWave5`→`NumericCov`

With this done, go to
`Config`→`Drivers`→`NHaystackNetwork`→`UpstreamNHaystack`→`Histories`,
right-click on the `Histories` node and select `Actions`→`Retry`.

## Testing and verification

With the stations running, leave the test set-up going for about 10 minutes.

When you return, check the histories of `nhaystack_receiver`, you should find
that the history data is present from and tracking `nhaystack_sender`.
