package com.juan.spongeskills.skills;

import ninja.leaping.configurate.ConfigurationNode;

public interface Skill {
    String SKILLS[]= {"mining", "woodcutting", "farming"};
    String getSkillName();
    String[][] getExpValues();
    void setupConfig(ConfigurationNode skillsConfig);
}
