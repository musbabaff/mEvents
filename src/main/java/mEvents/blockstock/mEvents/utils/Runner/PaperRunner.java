package mEvents.blockstock.mEvents.utils.Runner;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PaperRunner implements Runner {
    private final mEvents.blockstock.mEvents.mEvents plugin;

    public PaperRunner(mEvents.blockstock.mEvents.mEvents plugin) {
        this.plugin = plugin;
    }

    @Override
    public void runPlayer(@NotNull Runnable task, @NotNull Player player) {
        Bukkit.getScheduler().runTask(plugin, task);
    }

    @Override
    public void run(@NotNull Runnable task) {
        Bukkit.getScheduler().runTask(plugin, task);
    }

    @Override
    public void runAsync(@NotNull Runnable task) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, task);
    }

    @Override
    public void runDelayed(@NotNull Runnable task, long delayTicks) {
        Bukkit.getScheduler().runTaskLater(plugin, task, delayTicks);
    }

    @Override
    public void runDelayedAsync(@NotNull Runnable task, long delayTicks) {
        Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, task, delayTicks);
    }

    @Override
    public void runPeriodical(@NotNull Runnable task, long delayTicks, long periodTicks) {
        Bukkit.getScheduler().runTaskTimer(plugin, task, delayTicks, periodTicks);
    }

    @Override
    public void runPeriodicalAsync(@NotNull Runnable task, long delayTicks, long periodTicks) {
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, task, delayTicks, periodTicks);
    }

    @Override
    public void cancelTasks() {
        Bukkit.getScheduler().cancelTasks(plugin);
    }
}
