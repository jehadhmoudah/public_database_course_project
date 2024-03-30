import com.formdev.flatlaf.*;
import com.formdev.flatlaf.intellijthemes.*;
import com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatGitHubDarkIJTheme;
import com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatGitHubIJTheme;
import com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatMaterialLighterIJTheme;
import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import com.formdev.flatlaf.themes.FlatMacLightLaf;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

public class Themes {
    static Map<String, LookAndFeel> map = new HashMap<>();
    Themes () {
        map.put("IntelliJ", new FlatIntelliJLaf());
        map.put("Darcula", new FlatDarculaLaf());
        map.put("Mac Dark", new FlatMacDarkLaf());
        map.put("Mac Light", new FlatMacLightLaf());
        map.put("High Contrast", new FlatHighContrastIJTheme());
        map.put("Gradianto Nature Green", new FlatGradiantoNatureGreenIJTheme());
        map.put("Material Lighter", new FlatMaterialLighterIJTheme());
        map.put("GitHub", new FlatGitHubIJTheme());
        map.put("GitHub Dark", new FlatGitHubDarkIJTheme());
        map.put("Gradianto Dark Fuchsia", new FlatGradiantoDarkFuchsiaIJTheme());
        map.put("Dark", new FlatDarkFlatIJTheme());
        map.put("Cyan Light", new FlatCyanLightIJTheme());
        map.put("Gruvbox Dark Soft", new FlatGruvboxDarkSoftIJTheme());
        map.put("Dracula", new FlatDraculaIJTheme());
        map.put("Cobalt2", new FlatCobalt2IJTheme());
        map.put("Gruvbox Dark Medium", new FlatGruvboxDarkMediumIJTheme());
        map.put("Hiberbee Dark", new FlatHiberbeeDarkIJTheme());
        map.put("Gray", new FlatGrayIJTheme());
        map.put("Arc", new FlatArcIJTheme());
        map.put("Arc Dark", new FlatArcDarkIJTheme());
        map.put("Arc Orange", new FlatArcOrangeIJTheme());
        map.put("Gradianto Midnight Blue", new FlatGradiantoMidnightBlueIJTheme());
        map.put("Gradianto Deep Ocean", new FlatGradiantoDeepOceanIJTheme());
        map.put("Dark Purple", new FlatDarkPurpleIJTheme());
        map.put("Gruvbox Dark Hard", new FlatGruvboxDarkHardIJTheme());
        map.put("Arc Dark Orange", new FlatArcDarkOrangeIJTheme());
        map.put("Carbon", new FlatCarbonIJTheme());
        map.put("Monocai", new FlatMonocaiIJTheme());
        map.put("Light", new FlatLightFlatIJTheme());
        map.put("Material Design Dark", new FlatMaterialDesignDarkIJTheme());
        map.put("Monokai Pro", new FlatMonokaiProIJTheme());
        map.put("Nord", new FlatNordIJTheme());
        map.put("One Dark", new FlatOneDarkIJTheme());
        map.put("Solarized Dark", new FlatSolarizedDarkIJTheme());
        map.put("Solarized Light", new FlatSolarizedLightIJTheme());
        map.put("Spacegray", new FlatSpacegrayIJTheme());
        map.put("Vuesion", new FlatVuesionIJTheme());
        map.put("Xcode Dark", new FlatXcodeDarkIJTheme());
    }
}
