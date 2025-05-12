package model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author DELL
 */
public class SkillTreeManager implements Serializable {

    private SkillNode root;

    public SkillTreeManager() {
        buildSkillTree();
    }

    private void buildSkillTree() {
        root = new SkillNode("Conciencia Expandida", 2, null);

        SkillNode dominioLuz = new SkillNode("Dominio de Luz", 2, null);
        SkillNode dominioSombra = new SkillNode("Dominio de Sombra", 2, null);

        SkillNode luzCortante = new SkillNode("Luz Cortante +", 3, new Attack("Luz Cortante +", 40, "Ataque de luz mejorado", 1));
        SkillNode sombraDefensa = new SkillNode("Sombras de Defensa +", 3, null); // No es un ataque directo

        SkillNode selloSolar = new SkillNode("Sello Solar", 4, null); // Habilidad activa, no ataque directo
        SkillNode llamaNegra = new SkillNode("Llama Negra", 4, new Attack("Llama Negra", 35, "Ataque de sombra persistente", 1));

        SkillNode juicioFinal = new SkillNode("Juicio Final", 5, new Attack("Juicio Final", 70, "Ataque de luz definitivo", 1));
        SkillNode devoradorAlmas = new SkillNode("Devorador de Almas", 5, new Attack("Devorador de Almas", 60, "Ataque de sombra con robo de vida", 1));

        root.addChild(dominioLuz);
        root.addChild(dominioSombra);

        dominioLuz.addChild(luzCortante);
        dominioSombra.addChild(sombraDefensa);

        luzCortante.addChild(selloSolar);
        sombraDefensa.addChild(llamaNegra);

        selloSolar.addChild(juicioFinal);
        llamaNegra.addChild(devoradorAlmas);
    }

    public SkillNode getRoot() {
        return root;
    }

    public SkillNode findNodeByName(String name) {
        return findNodeByName(root, name);
    }

    private SkillNode findNodeByName(SkillNode node, String name) {
        if (node.getName().equals(name)) {
            return node;
        }
        for (SkillNode child : node.getChildren()) {
            SkillNode result = findNodeByName(child, name);
            if (result != null) {
                return result;
            }
        }
        return null;
    }
}
