scalability_stats <- function(operation, plot_title) {
  g_range <- range(0, scalability[[operation]]$alibaba, scalability[[operation]]$empire, scalability[[operation]]$jopa, scalability[[operation]]$komma, scalability[[operation]]$rdfbeans, na.rm=TRUE)
  mar.default <- c(5,4,4,2) + 0.1
  par(mar = mar.default + c(0, 1, 0, 0)) 
  plot(scalability[[operation]]$alibaba, type="o", ylim=g_range, axes=FALSE, ann=FALSE)
  title(plot_title)
  #title(ylab="Time/ms")
  mtext(text="Time (ms)", side=2, line=4)
  axis(1, at=1:6, lab=c("32", "64", "128", "256", "512", "1024"))
  axis(2, las=1, at=5000*0:g_range[2])
  title(xlab="Heap (MB)")
  box()
  lines(scalability[[operation]]$empire, type="o", pch=22, lty=2)
  lines(scalability[[operation]]$jopa, type="o", pch=23, lty=3)
  lines(scalability[[operation]]$komma, type="o", pch=24, lty=4)
  lines(scalability[[operation]]$rdfbeans, type="o", pch=25, lty=5)
  if (operation == 'three') {
    # Different placement of the legend so that it does not interfere with the plot lines
    legend(1, g_range[2]-22000, c("AliBaba", "Empire", "JOPA", "KOMMA", "RDFBeans"), pch=21:25, lty=1:5, bty="n")  
  } else {
    legend(1, g_range[2]-10000, c("AliBaba", "Empire", "JOPA", "KOMMA", "RDFBeans"), pch=21:25, lty=1:5, bty="n")
  }
}

##
# Creates heap-based scalability plots for each of the benchmark operations
#
# target_dir - directory into which the plots should be written
##
plot_scalability <- function(target_dir) {
  setEPS()
  #OP1
  pdf(paste(target_dir, 'scalability-op1-appendix.pdf', sep=''))
  scalability_stats('one', 'OP1 - Create')
  dev.off()
  
  #OP2
  pdf(paste(target_dir, 'scalability-op2-appendix.pdf', sep=''))
  scalability_stats('two', 'OP2 - Batch create')
  dev.off()
  
  #OP3
  pdf(paste(target_dir, 'scalability-op3-appendix.pdf', sep=''))
  scalability_stats('three', 'OP3 - Batch create')
  dev.off()
  
  #OP4
  pdf(paste(target_dir, 'scalability-op4-appendix.pdf', sep=''))
  scalability_stats('four', 'OP4 - Retrieve all')
  dev.off()
  
  #OP5
  pdf(paste(target_dir, 'scalability-op5-appendix.pdf', sep=''))
  scalability_stats('five', 'OP5 - Update')
  dev.off()
  
  #OP6
  pdf(paste(target_dir, 'scalability-op6-appendix.pdf', sep=''))
  scalability_stats('six', 'OP6 - Delete')
  dev.off()
}