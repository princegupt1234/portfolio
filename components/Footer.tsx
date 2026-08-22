"use client";

import { motion } from "framer-motion";
import { Heart } from "lucide-react";
import { FaLinkedin, FaGithub } from "react-icons/fa";

const Footer = () => {
  const currentYear = new Date().getFullYear();

  return (
    <footer className="bg-gray-900 text-white py-12">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="text-center">
          <motion.div
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.8 }}
            className="mb-6"
          >
            <h3 className="text-2xl font-bold mb-2">Prince Gupt</h3>
            <p className="text-gray-400">Full Stack Developer</p>
          </motion.div>

          <motion.div
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.8, delay: 0.2 }}
            className="flex justify-center gap-6 mb-8"
          >
            <a
              href="https://www.linkedin.com/in/prince-gupt-175289322"
              target="_blank"
              rel="noopener noreferrer"
              className="text-gray-400 hover:text-blue-400 transition-colors"
            >
              <FaLinkedin size={24} />
            </a>
            <a
              href="https://github.com/princegupt"
              target="_blank"
              rel="noopener noreferrer"
              className="text-gray-400 hover:text-white transition-colors"
            >
              <FaGithub size={24} />
            </a>
          </motion.div>

          <motion.div
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            transition={{ duration: 0.8, delay: 0.4 }}
            className="border-t border-gray-800 pt-8"
          >
            <p className="text-gray-400 flex items-center justify-center gap-2">
              Made with <Heart size={16} className="text-red-500" /> by Prince Gupt
            </p>
            <p className="text-gray-500 text-sm mt-2">
              © {currentYear} All rights reserved.
            </p>
          </motion.div>
        </div>
      </div>
    </footer>
  );
};

export default Footer;