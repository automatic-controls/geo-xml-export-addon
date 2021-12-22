# GeoTreeExportXML

This *WebCTRL* add-on exports structural geographic tree data as an *XML* file intended for use in *Inkscape* to create *SVG* graphics. The graphics markup tool provided by *SiteBuilder* serves a similar purpose; however, the markup tool includes lots of irrelevant information, and the *WebCTRL* server must be shutdown in order to use *SiteBuilder*. This add-on only includes the necessary information, which speeds up *XML* generation and decreases file-size by as much as *95%*.

*Inkscape* uses the *XML* file to create graphical associations that are linked into *WebCTRL* by the reference names of the corresponding locations on the geographic tree. *Inkscape* populates the label text for a given association by looking at the display name provided by the *XML* file. By default, the display names in the downloaded *XML* file exactly match the display names on the geographic tree. Entries in the *XML* file are ordered using a custom alpha-numeric sort.

## Example Generated *XML*

```xml
<DATA>
  <AREA name="Test System" ref="geographic">
    <AREA name="Main Building" ref="#main_building">
      <AREA name="Lower Lever" ref="#lower_lever">
        <EQUIPMENT name="AHU-1" ref="#ahu-1"></EQUIPMENT>
        <EQUIPMENT name="AHU-2" ref="#ahu-2"></EQUIPMENT>
        <EQUIPMENT name="AHU-3" ref="#ahu-3"></EQUIPMENT>
      </AREA>
      <AREA name="Main Level" ref="#main_level">
        <EQUIPMENT name="AHU-4" ref="#ahu-4"></EQUIPMENT>
        <EQUIPMENT name="AHU-5" ref="#ahu-5"></EQUIPMENT>
      </AREA>
      <AREA name="Roof" ref="#roof">
        <EQUIPMENT name="RTU-1" ref="#rtu-1"></EQUIPMENT>
        <EQUIPMENT name="RTU-2" ref="#rtu-2"></EQUIPMENT>
        <EQUIPMENT name="RTU-3" ref="#rtu-3"></EQUIPMENT>
        <EQUIPMENT name="RTU-4" ref="#rtu-4"></EQUIPMENT>
        <EQUIPMENT name="RTU-5" ref="#rtu-5"></EQUIPMENT>
      </AREA>
    </AREA>
    <EQUIPMENT name="Weather" ref="#weather"></EQUIPMENT>
  </AREA>
</DATA>
```

## Basic Usage

If your server requires signed add-ons, copy the authenticating certificate [*ACES.cer*](https://github.com/automatic-controls/webctrl-addon-dev/blob/main/ACES.cer?raw=true) to the *./addons* directory of your *WebCTRL* installation folder. Install [*GeoTreeExportXML.addon*](https://github.com/automatic-controls/geo-xml-export-addon/releases/latest/download/GeoTreeExportXML.addon) using the *WebCTRL* interface. Navigate to this add-on's main page (e.g, *localhost/GeoTreeExportXML*) and press the *Download* button.

## Advanced Usage

Regular expression transforms can be applied to display names. Regular expression syntax can be found in *Oracle*'s [documentation](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/regex/Pattern.html). These transforms allow you to preprocess the label text used by *Inkscape*. To preview the result of a transform before downloading, click the *Preview* button. You can save regular expressions for later by pressing the *Save* button. To select previously saved regex, click the button corresponding to your regex. When a regex is selected, the *Save* button overwrites the current regex contents (instead of prompting you to save a new regex). To delete the selected regex, press the *Clear* and *Save* buttons. Refer to the following table for example regular expression transforms.

| Find | Replace |
| - | - |
| `^\D*+(\d++).*+$` | `$1` |
| `^.*?(?>Room\|RM)\s*+(\d++).*+$` | `$1` |
| `^.*?(\w++(?>\s*+[\.\-]\s*+\d++)++).*+$` | `$1` |