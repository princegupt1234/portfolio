"use client";

import { motion } from "framer-motion";
import { useInView } from "framer-motion";
import { useRef, useState } from "react";
import { Code, Database, Server, Wrench, Star, TrendingUp, Zap } from "lucide-react";

const Skills = () => {
  const ref = useRef(null);
  const isInView = useInView(ref, { once: true });
  const [hoveredSkill, setHoveredSkill] = useState<string | null>(null);

  const skillCategories = [
    {
      title: "Frontend",
      icon: <Code className="w-8 h-8" />,
      color: "from-blue-500 to-cyan-500",
      bgColor: "from-blue-500/10 to-cyan-500/10",
      borderColor: "border-blue-500/20",
      skills: [
        { name: "React", level: 95, icon: "⚛️", description: "Advanced component architecture & hooks" },
        { name: "TypeScript", level: 90, icon: "🔷", description: "Type-safe development & advanced types" },
        { name: "Next.js", level: 88, icon: "▲", description: "Full-stack React framework" },
        { name: "Tailwind CSS", level: 92, icon: "🎨", description: "Utility-first CSS framework" },
      ],
    },
    {
      title: "Backend",
      icon: <Server className="w-8 h-8" />,
      color: "from-green-500 to-emerald-500",
      bgColor: "from-green-500/10 to-emerald-500/10",
      borderColor: "border-green-500/20",
      skills: [
        { name: "Node.js", level: 85, icon: "🟢", description: "Server-side JavaScript runtime" },
        { name: "Express.js", level: 82, icon: "🚀", description: "Fast, unopinionated web framework" },
        { name: "Python", level: 78, icon: "🐍", description: "Data processing & automation" },
        { name: "PHP", level: 80, icon: "🐘", description: "Web development & server scripting" },
      ],
    },
    {
      title: "Database",
      icon: <Database className="w-8 h-8" />,
      color: "from-purple-500 to-pink-500",
      bgColor: "from-purple-500/10 to-pink-500/10",
      borderColor: "border-purple-500/20",
      skills: [
        { name: "MySQL", level: 85, icon: "🐘", description: "Advanced relational database" },
        { name: "MongoDB", level: 80, icon: "🍃", description: "NoSQL document database" },
        { name: "PostgreSQL", level: 78, icon: "🐦", description: "Powerful open-source RDBMS" },
      ],
    },
    
    {
  title: "DSA",
  icon: <Code className="w-8 h-8" />,
  color: "from-blue-500 to-cyan-500",
  bgColor: "from-blue-500/10 to-cyan-500/10",
  borderColor: "border-blue-500/20",
  skills: [
    { name: "Arrays", level: 10, icon: "📊", description: "Traversal, prefix sum, sliding window techniques" },
    { name: "Linked List", level: 9, icon: "🔗", description: "Pointer manipulation and efficient operations" },
    { name: "Stack & Queue", level: 0, icon: "📚", description: "LIFO/FIFO structures and applications" },
   
  ],
},
  ];

  return (
    <section id="skills" className="py-20 bg-gradient-to-br from-gray-50 to-white dark:from-gray-900 dark:to-gray-800 relative overflow-hidden">
      {/* Background Pattern */}
      <div className="absolute inset-0 opacity-5">
        <div className="absolute inset-0" style={{
          backgroundImage: `url("data:image/svg+xml,%3Csvg width='40' height='40' viewBox='0 0 40 40' xmlns='http://www.w3.org/2000/svg'%3E%3Cg fill='%239C92AC' fill-opacity='0.1'%3E%3Cpath d='M20 20c0-5.5-4.5-10-10-10s-10 4.5-10 10 4.5 10 10 10 10-4.5 10-10zm10 0c0-5.5-4.5-10-10-10s-10 4.5-10 10 4.5 10 10 10 10-4.5 10-10z'/%3E%3C/g%3E%3C/svg%3E")`,
        }} />
      </div>

      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 relative">
        <motion.div
          ref={ref}
          initial={{ opacity: 0, y: 50 }}
          animate={isInView ? { opacity: 1, y: 0 } : {}}
          transition={{ duration: 0.8 }}
          className="text-center mb-16"
        >
          <div className="inline-flex items-center gap-3 bg-gradient-to-r from-blue-600 to-purple-600 text-white px-6 py-3 rounded-full font-semibold text-lg mb-6">
            <Star className="w-5 h-5" />
            Technical Expertise
          </div>
          <h2 className="text-4xl md:text-5xl font-bold bg-gradient-to-r from-gray-900 to-gray-600 dark:from-white dark:to-gray-300 bg-clip-text text-transparent mb-6">
            Skills & Technologies
          </h2>
          <p className="text-xl text-gray-600 dark:text-gray-300 max-w-3xl mx-auto leading-relaxed">
            A comprehensive toolkit of modern technologies and frameworks, continuously evolving with industry trends
          </p>
        </motion.div>

        <div className="grid md:grid-cols-2 lg:grid-cols-4 gap-8">
          {skillCategories.map((category, categoryIndex) => (
            <motion.div
              key={category.title}
              initial={{ opacity: 0, y: 50 }}
              animate={isInView ? { opacity: 1, y: 0 } : {}}
              transition={{ duration: 0.8, delay: categoryIndex * 0.1 }}
              className={`bg-gradient-to-br ${category.bgColor} backdrop-blur-sm border ${category.borderColor} rounded-2xl p-6 hover:shadow-xl transition-all duration-300 hover:scale-105 group`}
            >
              <div className={`inline-flex items-center justify-center w-16 h-16 rounded-xl bg-gradient-to-r ${category.color} text-white mb-6 group-hover:scale-110 transition-transform shadow-lg`}>
                {category.icon}
              </div>
              <h3 className="text-xl font-bold text-gray-900 dark:text-white mb-6 text-center">
                {category.title}
              </h3>
              <div className="space-y-4">
                {category.skills.map((skill, skillIndex) => (
                  <div
                    key={skill.name}
                    className="group/skill relative"
                    onMouseEnter={() => setHoveredSkill(skill.name)}
                    onMouseLeave={() => setHoveredSkill(null)}
                  >
                    <div className="flex justify-between text-sm text-gray-600 dark:text-gray-300 mb-2">
                      <div className="flex items-center gap-2">
                        <span className="text-lg">{skill.icon}</span>
                        <span className="font-medium">{skill.name}</span>
                      </div>
                      <span className="font-semibold text-gray-900 dark:text-white">{skill.level}%</span>
                    </div>
                    <div className="w-full bg-gray-200 dark:bg-gray-700 rounded-full h-3 overflow-hidden">
                      <motion.div
                        initial={{ width: 0 }}
                        animate={isInView ? { width: `${skill.level}%` } : {}}
                        transition={{ duration: 1.5, delay: categoryIndex * 0.1 + skillIndex * 0.1, ease: "easeOut" }}
                        className={`h-3 rounded-full bg-gradient-to-r ${category.color} relative`}
                      >
                        <div className="absolute inset-0 bg-white/20 rounded-full animate-pulse"></div>
                      </motion.div>
                    </div>

                    {/* Tooltip */}
                    <motion.div
                      initial={{ opacity: 0, scale: 0.8 }}
                      animate={{
                        opacity: hoveredSkill === skill.name ? 1 : 0,
                        scale: hoveredSkill === skill.name ? 1 : 0.8
                      }}
                      transition={{ duration: 0.2 }}
                      className="absolute -top-12 left-1/2 transform -translate-x-1/2 bg-gray-900 text-white text-xs px-3 py-2 rounded-lg whitespace-nowrap z-10 shadow-lg"
                    >
                      {skill.description}
                      <div className="absolute top-full left-1/2 transform -translate-x-1/2 w-0 h-0 border-l-4 border-r-4 border-t-4 border-transparent border-t-gray-900"></div>
                    </motion.div>
                  </div>
                ))}
              </div>

              {/* Category Stats */}
              <div className="mt-6 pt-4 border-t border-gray-200 dark:border-gray-600">
                <div className="flex items-center justify-between text-xs text-gray-500 dark:text-gray-400">
                  <span>Avg. Proficiency</span>
                  <div className="flex items-center gap-1">
                    <TrendingUp className="w-3 h-3 text-green-500" />
                    <span className="text-green-500 font-medium">
                      {Math.round(category.skills.reduce((acc, skill) => acc + skill.level, 0) / category.skills.length)}%
                    </span>
                  </div>
                </div>
              </div>
            </motion.div>
          ))}
        </div>

        {/* Additional Skills Showcase */}
        <motion.div
          initial={{ opacity: 0, y: 30 }}
          animate={isInView ? { opacity: 1, y: 0 } : {}}
          transition={{ duration: 0.8, delay: 0.6 }}
          className="mt-16 bg-gradient-to-r from-blue-50 to-purple-50 dark:from-gray-800 dark:to-gray-700 rounded-2xl p-8 border border-blue-100 dark:border-gray-600"
        >
          <div className="text-center mb-8">
            <h3 className="text-2xl font-bold text-gray-900 dark:text-white mb-2">Continuous Learning</h3>
            <p className="text-gray-600 dark:text-gray-300">Always expanding my technical horizons</p>
          </div>

          <div className="grid md:grid-cols-3 gap-6">
            {[
              { name: "Machine Learning", progress: 3, icon: "🤖", description: "TensorFlow, PyTorch basics" },
              { name: "Blockchain", progress: 0, icon: "⛓️", description: "Smart contracts, Web3" },
              { name: "DSA", progress: 12, icon: "🧩", description: "Arrays, trees, graphs, DP, and problem-solving" }
            ].map((skill, index) => (
              <motion.div
                key={skill.name}
                initial={{ opacity: 0, scale: 0.9 }}
                animate={isInView ? { opacity: 1, scale: 1 } : {}}
                transition={{ duration: 0.6, delay: 0.8 + index * 0.1 }}
                className="bg-white dark:bg-gray-800 rounded-xl p-6 shadow-lg border border-gray-100 dark:border-gray-600 hover:shadow-xl transition-shadow"
              >
                <div className="flex items-center gap-3 mb-4">
                  <span className="text-2xl">{skill.icon}</span>
                  <div>
                    <h4 className="font-semibold text-gray-900 dark:text-white">{skill.name}</h4>
                    <p className="text-sm text-gray-600 dark:text-gray-400">{skill.description}</p>
                  </div>
                </div>
                <div className="w-full bg-gray-200 dark:bg-gray-700 rounded-full h-2">
                  <motion.div
                    initial={{ width: 0 }}
                    animate={isInView ? { width: `${skill.progress}%` } : {}}
                    transition={{ duration: 1, delay: 1 + index * 0.1 }}
                    className="bg-gradient-to-r from-yellow-400 to-orange-500 h-2 rounded-full"
                  />
                </div>
                <div className="flex justify-between items-center mt-2">
                  <span className="text-xs text-gray-500 dark:text-gray-400">Learning Progress</span>
                  <span className="text-sm font-medium text-gray-900 dark:text-white">{skill.progress}%</span>
                </div>
              </motion.div>
            ))}
          </div>
        </motion.div>
      </div>
    </section>
  );
};

export default Skills;