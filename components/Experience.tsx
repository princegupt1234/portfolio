"use client";

import { motion } from "framer-motion";
import { useInView } from "framer-motion";
import { useRef } from "react";
import { Trophy, BookOpen, Code2, Users } from "lucide-react";

const Experience = () => {
  const ref = useRef(null);
  const isInView = useInView(ref, { once: true });

  const experiences = [
    {
      icon: <Trophy className="w-8 h-8" />,
      title: "Hackathon Participation",
      description: "Participated in multiple coding hackathons, developing innovative solutions under time constraints and collaborating with diverse teams.",
      achievements: ["Won 2nd place in Local Hackathon", "Built real-time collaboration tool", "Learned agile development"],
    },
    {
      icon: <BookOpen className="w-8 h-8" />,
      title: "Self-Learning Journey",
      description: "Dedicated time to continuous learning through online courses, documentation, and hands-on projects to stay updated with industry trends.",
      achievements: ["Completed 15+ online courses", "Built 20+ personal projects", "Mastered React ecosystem"],
    },
    {
      icon: <Code2 className="w-8 h-8" />,
      title: "Technology Exploration",
      description: "Explored various technologies and frameworks, from frontend libraries to backend architectures, to broaden technical expertise.",
      achievements: ["Full-stack development", "Database design", "API development", "Version control"],
    },
  ];

  return (
    <section id="experience" className="py-20 bg-gray-50 dark:bg-gray-800">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <motion.div
          ref={ref}
          initial={{ opacity: 0, y: 50 }}
          animate={isInView ? { opacity: 1, y: 0 } : {}}
          transition={{ duration: 0.8 }}
          className="text-center mb-16"
        >
          <h2 className="text-3xl md:text-4xl font-bold text-gray-900 dark:text-white mb-4">
            Experience & Learning Journey
          </h2>
          <p className="text-lg text-gray-600 dark:text-gray-300 max-w-2xl mx-auto">
            My path of growth through challenges, learning, and continuous improvement
          </p>
        </motion.div>

        <div className="grid md:grid-cols-1 lg:grid-cols-3 gap-8">
          {experiences.map((exp, index) => (
            <motion.div
              key={exp.title}
              initial={{ opacity: 0, y: 50 }}
              animate={isInView ? { opacity: 1, y: 0 } : {}}
              transition={{ duration: 0.8, delay: index * 0.2 }}
              className="bg-white dark:bg-gray-900 rounded-xl p-6 shadow-lg hover:shadow-xl transition-shadow"
            >
              <div className="flex items-center gap-4 mb-4">
                <div className="p-3 bg-blue-100 dark:bg-blue-900/30 rounded-lg text-blue-600 dark:text-blue-400">
                  {exp.icon}
                </div>
                <h3 className="text-xl font-semibold text-gray-900 dark:text-white">
                  {exp.title}
                </h3>
              </div>

              <p className="text-gray-600 dark:text-gray-300 mb-4">
                {exp.description}
              </p>

              <div>
                <h4 className="text-lg font-semibold text-gray-900 dark:text-white mb-2">
                  Key Achievements
                </h4>
                <ul className="space-y-2">
                  {exp.achievements.map((achievement, i) => (
                    <li key={i} className="flex items-center gap-2 text-gray-600 dark:text-gray-300">
                      <div className="w-1.5 h-1.5 bg-blue-600 rounded-full"></div>
                      {achievement}
                    </li>
                  ))}
                </ul>
              </div>
            </motion.div>
          ))}
        </div>
      </div>
    </section>
  );
};

export default Experience;