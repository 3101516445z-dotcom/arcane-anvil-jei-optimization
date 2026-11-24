package io.github.arcaneanviljeioptimization;

import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.service.MixinService;

final class ApiChecks {
    interface Nodes {
        ClassNode get(String name) throws Exception;
    }

    static final Nodes LIVE =
            name ->
                    MixinService.getService()
                            .getBytecodeProvider()
                            .getClassNode(name.replace('/', '.'));

    static boolean method(Nodes nodes, String owner, String name, String desc, int mask, int flags)
            throws Exception {
        return nodes.get(owner).methods.stream()
                .anyMatch(
                        m ->
                                m.name.equals(name)
                                        && m.desc.equals(desc)
                                        && (m.access & mask) == flags);
    }

    static boolean field(Nodes nodes, String owner, String name, String desc, int mask, int flags)
            throws Exception {
        return nodes.get(owner).fields.stream()
                .anyMatch(
                        f ->
                                f.name.equals(name)
                                        && f.desc.equals(desc)
                                        && (f.access & mask) == flags);
    }
}
