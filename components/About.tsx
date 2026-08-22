"use client";

import { motion } from "framer-motion";
import { useInView } from "framer-motion";
import { useRef } from "react";
import { Code, Lightbulb, Target } from "lucide-react";

const About = () => {
  const ref = useRef(null);
  const isInView = useInView(ref, { once: true });

  return (
    <section id="about" className="py-20 bg-white dark:bg-gray-900">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <motion.div
          ref={ref}
          initial={{ opacity: 0, y: 50 }}
          animate={isInView ? { opacity: 1, y: 0 } : {}}
          transition={{ duration: 0.8 }}
          className="text-center mb-16"
        >
          <h2 className="text-3xl md:text-4xl font-bold text-gray-900 dark:text-white mb-4">
            About Me
          </h2>
          <p className="text-lg text-gray-600 dark:text-gray-300 max-w-3xl mx-auto">
            Passionate about creating digital solutions that make a difference
          </p>
        </motion.div>

        <div className="grid md:grid-cols-2 gap-12 items-center">
          <motion.div
            initial={{ opacity: 0, x: -50 }}
            animate={isInView ? { opacity: 1, x: 0 } : {}}
            transition={{ duration: 0.8, delay: 0.2 }}
          >
            <div className="bg-gradient-to-br from-blue-500 to-purple-600 rounded-2xl p-8 text-white">
              <h3 className="text-2xl font-bold mb-4">My Journey</h3>
              <p className="text-blue-100 leading-relaxed">
                Started my coding journey with curiosity and a drive to solve real-world problems.
                Through self-learning and hands-on projects, I've developed a strong foundation in
                full-stack development. I believe in writing clean, maintainable code and staying
                updated with the latest technologies.
              </p>
            </div>
          </motion.div>

          <motion.div
            initial={{ opacity: 0, x: 50 }}
            animate={isInView ? { opacity: 1, x: 0 } : {}}
            transition={{ duration: 0.8, delay: 0.4 }}
            className="space-y-6"
          >
            <div className="flex items-start gap-4">
              <div className="p-3 bg-blue-100 dark:bg-blue-900/30 rounded-lg">
                <Code className="w-6 h-6 text-blue-600 dark:text-blue-400" />
              </div>
              <div>
                <h4 className="text-xl font-semibold text-gray-900 dark:text-white mb-2">
                  Problem-Solving Mindset
                </h4>
                <p className="text-gray-600 dark:text-gray-300">
                  I approach every challenge as an opportunity to learn and innovate,
                  breaking down complex problems into manageable solutions.
                </p>
              </div>
            </div>

            <div className="flex items-start gap-4">
              <div className="p-3 bg-green-100 dark:bg-green-900/30 rounded-lg">
                <Lightbulb className="w-6 h-6 text-green-600 dark:text-green-400" />
              </div>
              <div>
                <h4 className="text-xl font-semibold text-gray-900 dark:text-white mb-2">
                  Continuous Learning - DSA
                </h4>
                <p className="text-gray-600 dark:text-gray-300">
                  Always expanding my technical horizons through Data Structures and Algorithms.
                </p>
              </div>
            </div>

            <div className="flex items-start gap-4">
              <div className="p-3 bg-purple-100 dark:bg-purple-900/30 rounded-lg">
                <Target className="w-6 h-6 text-purple-600 dark:text-purple-400" />
              </div>
              <div>
                <h4 className="text-xl font-semibold text-gray-900 dark:text-white mb-2">
                  Passion for Development
                </h4>
                <p className="text-gray-600 dark:text-gray-300">
                  I love turning ideas into reality through code, creating applications
                  that are both functional and beautiful.
                </p>
              </div>
            </div>
          </motion.div>
        </div>
      </div>
    </section>
  );
};

export default About;